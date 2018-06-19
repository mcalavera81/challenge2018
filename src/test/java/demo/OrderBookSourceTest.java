package demo;

import demo.order.domain.OrderBookSnapshot;
import demo.shared.parser.UtilParser.BitsoBook;
import demo.order.service.RestOrderBookSnapshotSource;
import demo.shared.config.AppConfiguration;
import io.vavr.control.Try;
import lombok.val;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static demo.TestUtils.loadJson;
import static demo.TestUtils.withDefault;
import static junit.framework.TestCase.assertTrue;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class OrderBookSourceTest {


    private RestOrderBookSnapshotSource source;
    private RestOrderBookSnapshotSource sourceWithLimiter;

    private AppConfiguration.BackendConfig conf;

    @Before
    public void init() {
        conf = AppConfiguration.tryBuild().getBackendConfig();

    }

    @Test
    public void test_get_bitso_OrderBook() {
        source = new RestOrderBookSnapshotSource(
            BitsoBook.BTC_MXN,
            conf.getBitsoRestUri(),
            asyncHttpClient());

        Try<OrderBookSnapshot> orderBook = source.getOrderBook();
        assertThat(orderBook.isSuccess(), is(true));
    }

    @Test
    @SuppressWarnings("unckecked")
    public void test_get_bitso_OrderBook_with_limiter() throws Exception {

        // ------------------ GIVEN ----------------------------
        String filename = "order/order_book_ok.json";
        String book = loadJson(filename).get().toString();

        AsyncHttpClient httpClient = mock(AsyncHttpClient.class, RETURNS_DEEP_STUBS);
        BoundRequestBuilder boundRequestBuilderMock = mock
            (BoundRequestBuilder.class);
        ListenableFuture<Response> listenableFutureMock = mock
            (ListenableFuture.class);
        Response responseMock = mock(Response.class);


        when(httpClient.prepareGet(anyString())).thenReturn
            (boundRequestBuilderMock);
        when(boundRequestBuilderMock.setHeader(anyString(), anyString()))
            .thenReturn(boundRequestBuilderMock);
        when(boundRequestBuilderMock.execute()).thenReturn(listenableFutureMock);
        when(listenableFutureMock.get()).thenReturn(responseMock);
        when(responseMock.getResponseBody()).thenReturn( book);

        sourceWithLimiter = new RestOrderBookSnapshotSource(
            BitsoBook.BTC_MXN,
            conf.getBitsoRestUri(),
            httpClient,
            1,
            5);



        // ------------------- WHEN --------------------
        val orderBook = sourceWithLimiter.getOrderBook();

        Duration FAILURE_TIMEOUT = Duration.ofMillis(4500);

        val failure1= withDefault(
            CompletableFuture.supplyAsync(() -> sourceWithLimiter.getOrderBook()),
            Try.failure(new RuntimeException()),
            FAILURE_TIMEOUT);


        val failure2= withDefault(
            CompletableFuture.supplyAsync(() -> sourceWithLimiter.getOrderBook()),
            Try.failure(new RuntimeException()),
            FAILURE_TIMEOUT);


        // ------------------- THEN --------------------
        assertTrue(orderBook.isSuccess());
        assertTrue(failure1.get().isFailure());
        assertTrue(failure2.get().isFailure());

        Mockito.verify(httpClient, Mockito.times(1))
            .prepareGet(Mockito.anyString());


    }

}
