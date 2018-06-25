package demo;


import demo.app.backend.InMemoryBackend;
import demo.app.config.AppConfiguration;
import demo.order.business.processor.BookSynchronizer;
import demo.order.business.state.OrderBook;
import demo.order.source.stream.client.DiffOrderStreamConsumer;
import demo.trade.business.state.RecentTradesLog;
import lombok.val;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InMemoryBackendTest {


    @Mock
    private BookSynchronizer bookSynchronizer;
    @Mock
    private OrderBook orderBook;
    @Mock
    private DiffOrderStreamConsumer streamConsumer;
    @Mock
    private RecentTradesLog recentTradesLog;

    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void test_backend_build_ok() {

        val conf = AppConfiguration.getInstance();
        val backend = InMemoryBackend.build(conf.getBackendConfig());

        Assert.assertNotNull(backend);
        Assert.assertNotNull(backend.getOrderBook());
        Assert.assertNotNull(backend.getRecentTradesLog());

        Assert.assertFalse(backend.isRunning());
        backend.start();
        Assert.assertTrue(backend.isRunning());
        backend.stop();
        Assert.assertFalse(backend.isRunning());

    }

    @Test
    public void test_backend_builder_book_synchronizer_null() {


        Assertions.assertThrows(NullPointerException.class,
            ()->InMemoryBackend.builder()
                .bookSynchronizer(null)
                .orderBook(orderBook)
                .orderStreamConsumer(streamConsumer)
                .recentTradesLog(recentTradesLog)
                .build());
    }

    @Test
    public void test_backend_builder_order_book_null() {

        Assertions.assertThrows(NullPointerException.class,
            ()->InMemoryBackend.builder()
            .bookSynchronizer(bookSynchronizer)
            .orderBook(null)
            .orderStreamConsumer(streamConsumer)
            .recentTradesLog(recentTradesLog)
            .build());
    }

    @Test
    public void test_backend_builder_stream_consumer_null() {

        Assertions.assertThrows(NullPointerException.class,
            ()->InMemoryBackend.builder()
                .bookSynchronizer(bookSynchronizer)
                .orderBook(orderBook)
                .orderStreamConsumer(null)
                .recentTradesLog(recentTradesLog)
                .build());
    }

    @Test
    public void test_backend_builder_recent_trades_log_null() {

        Assertions.assertThrows(NullPointerException.class,
            ()->InMemoryBackend.builder()
                .bookSynchronizer(bookSynchronizer)
                .orderBook(orderBook)
                .orderStreamConsumer(streamConsumer)
                .recentTradesLog(null)
                .build());
    }


    @Test
    public void test_backend_builder() {
        Assertions.assertThrows(NullPointerException.class,
            ()-> InMemoryBackend.build(null));
    }
}