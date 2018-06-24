package demo.app;

import demo.order.business.state.OrderBook;
import demo.order.business.state.SyncHashMapOrderBook;
import demo.order.business.processor.BookSynchronizer;
import demo.order.source.poller.client.RestOrderBookSnapshotSource;
import demo.order.source.stream.client.BitsoWebsocketListener;
import demo.order.source.stream.client.DiffOrderStreamConsumer;
import demo.order.source.stream.client.OrderBuffer;
import demo.shared.config.AppConfiguration.BackendConfig;
import demo.shared.parser.UtilParser;
import demo.trade.business.algorithm.ContrarianTradingStrategy;
import demo.trade.business.state.LatestTradesContainer;
import demo.trade.business.state.RecentTradesLog;
import demo.trade.source.poller.TradesSourceRest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.asynchttpclient.AsyncHttpClient;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Slf4j
public class InMemoryBackend implements Backend {

    private DiffOrderStreamConsumer orderStreamConsumer;
    private BookSynchronizer bookSynchronizer;

    private RecentTradesLog recentTradesLog;
    private OrderBook orderBook;

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

    @Override
    public RecentTradesLog getRecentTradesLog() {
        return recentTradesLog;
    }

    @Override
    public OrderBook getOrderBook() {
        return orderBook;
    }

    public static Backend build(@NonNull BackendConfig conf) {

        InMemoryBackend backend = new InMemoryBackend();

        backend.orderBook = new SyncHashMapOrderBook();
        OrderBuffer orderBuffer = new OrderBuffer(conf.getOrderStreamBuffer());
        AsyncHttpClient httpClient = asyncHttpClient();


        val streamListener = new BitsoWebsocketListener(orderBuffer);

        val tradesSource = new TradesSourceRest(
            UtilParser.BitsoBook.BTC_MXN,
            conf.getBitsoRestUri(),
            httpClient,
            1,
            5);

        backend.recentTradesLog = new LatestTradesContainer(
            tradesSource,
            conf.getTradesMemoryCacheSize(),
            conf.getTradesRestDefaultLimit(),
            new ContrarianTradingStrategy(
                conf.getTradingAlgorithmUpTicks(),
                conf.getTradingAlgorithmDownTicks())
        );

        val orderBookSource = new RestOrderBookSnapshotSource(
            UtilParser.BitsoBook.BTC_MXN,
            conf.getBitsoRestUri(),
            httpClient,
            1,
            5);

        backend.orderStreamConsumer = new DiffOrderStreamConsumer(
            conf.getBitsoWebsocketUri(),
            httpClient,
            streamListener);

        backend.bookSynchronizer = new BookSynchronizer(
            orderBuffer,
            backend.orderBook,
            orderBookSource
        );

        Runtime.getRuntime().addShutdownHook(
            new ShutdownResourcesCleanUp(
                backend.bookSynchronizer,
                backend.orderStreamConsumer)
        );
        return backend;
    }
}
