package demo;

import demo.order.domain.OrderBook;
import demo.trade.domain.ContrarianTradingStrategySimulator;
import demo.trade.domain.RecentTradesLog;
import demo.order.domain.SyncHashMapOrderBook;
import demo.trade.domain.LatestTradesContainer;
import demo.shared.parser.UtilParser;
import demo.order.service.BookSynchronizer;
import demo.order.service.RestOrderBookSnapshotSource;
import demo.order.service.websocket.BitsoWebsocketListener;
import demo.order.service.websocket.DiffOrderStreamConsumer;
import demo.order.service.websocket.OrderBuffer;
import demo.shared.config.AppConfiguration.BackendConfig;
import demo.trade.service.TradesSourceRest;
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
            new ContrarianTradingStrategySimulator(
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
