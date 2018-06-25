package demo.app.backend;

import demo.app.Constants;
import demo.app.config.AppConfiguration.BackendConfig;
import demo.order.business.processor.BookSynchronizer;
import demo.order.business.state.OrderBook;
import demo.order.business.state.SyncHashMapOrderBook;
import demo.order.source.poller.client.RestOrderBookSnapshotSource;
import demo.order.source.stream.client.BitsoWebsocketListener;
import demo.order.source.stream.client.DiffOrderStreamConsumer;
import demo.order.source.stream.client.OrderBuffer;
import demo.support.thread.ShutdownResourcesCleanUp;
import demo.trade.business.algorithm.ContrarianTradingStrategy;
import demo.trade.business.state.LatestTradesContainer;
import demo.trade.business.state.RecentTradesLog;
import demo.trade.source.poller.TradesSourceRest;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.asynchttpclient.AsyncHttpClient;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Slf4j
@RequiredArgsConstructor
public class InMemoryBackend implements Backend {

    @NonNull
    private final DiffOrderStreamConsumer orderStreamConsumer;
    @NonNull
    private final BookSynchronizer bookSynchronizer;
    @NonNull
    private final RecentTradesLog recentTradesLog;
    @NonNull
    private final OrderBook orderBook;

    private boolean isRunning = false;

    @Override
    public void start() {
        log.info("Starting the Backend...");
        orderStreamConsumer.start();
        bookSynchronizer.start();
        isRunning = true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void stop() {
        log.info("Stoping the Backend...");
        orderStreamConsumer.stop();
        bookSynchronizer.stop();
        isRunning = false;

    }

    @Builder(builderMethodName = "builder")
    public static InMemoryBackend newBackend(
        DiffOrderStreamConsumer orderStreamConsumer,
        BookSynchronizer bookSynchronizer,
        RecentTradesLog recentTradesLog,
        OrderBook orderBook) {

        return new InMemoryBackend(orderStreamConsumer,bookSynchronizer,
            recentTradesLog,orderBook);
    }

    public static Backend build(@NonNull BackendConfig conf) {


        OrderBuffer orderBuffer = new OrderBuffer(conf.getOrderStreamBuffer());
        AsyncHttpClient httpClient = asyncHttpClient();


        val streamListener = new BitsoWebsocketListener(orderBuffer);
        val tradesSource = newTradesSourceRest(conf, httpClient);
        val orderBookSource = newRestOrderBookSnapshotSource(conf, httpClient);
        val orderBook = new SyncHashMapOrderBook();

        InMemoryBackend backend = InMemoryBackend.builder()
            .bookSynchronizer(newBookSynchronizer(orderBook, orderBuffer, orderBookSource))
            .orderBook(orderBook)
            .orderStreamConsumer(diffOrderStreamConsumer(conf, httpClient,
                streamListener))
            .recentTradesLog(newTradesContainer(conf, tradesSource))
            .build();

        backend.setUpGracefulShutdown();

        return backend;
    }

    @Override
    public RecentTradesLog getRecentTradesLog() {
        return recentTradesLog;
    }

    @Override
    public OrderBook getOrderBook() {
        return orderBook;
    }


    private void setUpGracefulShutdown() {
        Runtime.getRuntime().addShutdownHook(
            new ShutdownResourcesCleanUp(
                this.bookSynchronizer,
                this.orderStreamConsumer)
        );
    }

    private static BookSynchronizer newBookSynchronizer(OrderBook orderBook,
                                                        OrderBuffer orderBuffer,
                                                        RestOrderBookSnapshotSource orderBookSource) {
        return new BookSynchronizer(
            orderBuffer,
            orderBook,
            orderBookSource
        );
    }

    private static DiffOrderStreamConsumer diffOrderStreamConsumer(
        BackendConfig conf,
        AsyncHttpClient httpClient,
        BitsoWebsocketListener streamListener) {

        return new DiffOrderStreamConsumer(
            conf.getBitsoWebsocketUri(),
            httpClient,
            streamListener);
    }

    private static RestOrderBookSnapshotSource newRestOrderBookSnapshotSource(
        BackendConfig conf,
        AsyncHttpClient httpClient) {

        return new RestOrderBookSnapshotSource(
            Constants.BitsoBook.BTC_MXN,
            conf.getBitsoRestUri(),
            httpClient,
            conf.getBitsoRestLimiterPermits(),
            conf.getBitsoRestLimiterPeriod());
    }

    private static LatestTradesContainer newTradesContainer(
        BackendConfig conf,
        TradesSourceRest tradesSource) {

        return new LatestTradesContainer(
            tradesSource,
            conf.getTradesMemoryCacheSize(),
            conf.getTradesRestDefaultLimit(),
            new ContrarianTradingStrategy(
                conf.getTradingAlgorithmUpTicks(),
                conf.getTradingAlgorithmDownTicks())
        );
    }

    private static TradesSourceRest newTradesSourceRest(
        BackendConfig conf,
        AsyncHttpClient httpClient) {

        return new TradesSourceRest(
            Constants.BitsoBook.BTC_MXN,
            conf.getBitsoRestUri(),
            httpClient,
            conf.getBitsoRestLimiterPermits(),
            conf.getBitsoRestLimiterPeriod());
    }
}
