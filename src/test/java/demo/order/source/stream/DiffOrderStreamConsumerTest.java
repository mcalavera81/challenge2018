package demo.order.source.stream;

import demo.order.source.stream.client.DiffOrderStreamConsumer;
import demo.app.config.AppConfiguration;
import demo.support.thread.ThreadRunner;
import lombok.val;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static demo.TestHelpers.withDefault;
import static junit.framework.TestCase.assertTrue;
import static org.asynchttpclient.Dsl.asyncHttpClient;

public class DiffOrderStreamConsumerTest {

    private static AsyncHttpClient httpClient;
    private static WebSocketListener mockListener;

    @BeforeClass
    public static void initClass(){
        httpClient = asyncHttpClient();
        mockListener = Mockito.mock(WebSocketListener.class);
    }


    @Test
    public void test_constructor(){
        Assertions.assertThrows(
            NullPointerException.class,
            ()->new DiffOrderStreamConsumer(
                null, httpClient, mockListener));

        Assertions.assertThrows(
            NullPointerException.class,
            ()->new DiffOrderStreamConsumer(
                "URI", null, mockListener));

        Assertions.assertThrows(
            NullPointerException.class,
            ()->new DiffOrderStreamConsumer(
                "URI", httpClient, null));
    }


    @Test
    public void test_wrong_websocket_uri(){
        ThreadRunner consumer = new DiffOrderStreamConsumer(
            "Wrong_URI",
            httpClient,
            getListener(new CompletableFuture<>(),new CompletableFuture<>()));

        Assertions.assertThrows(RuntimeException.class,
            consumer::start);
    }

    @Test
    public void test_stop_consumer_before_starting(){
        ThreadRunner consumer = new DiffOrderStreamConsumer(
            "Wrong_URI",
            httpClient,
            getListener(new CompletableFuture<>(),new CompletableFuture<>()));

        consumer.stop();
    }

    @Test
    public void test_subscription_diff_order_channel() throws Exception  {

        final AppConfiguration conf = AppConfiguration.getInstance();

        CompletableFuture<Boolean> subscriptionResponse = new CompletableFuture<>();
        CompletableFuture<Boolean> closedResponse = new CompletableFuture<>();

        ThreadRunner consumer = new DiffOrderStreamConsumer(
            conf.getBackendConfig().getBitsoWebsocketUri(),
            httpClient,
            getListener(subscriptionResponse,closedResponse));


        Duration FAILURE_TIMEOUT = Duration.ofMillis(3000);

        consumer.start();
        val subscriptionWithTimeout = withDefault(
            subscriptionResponse,
            Boolean.FALSE,
            FAILURE_TIMEOUT);

        assertTrue(subscriptionWithTimeout.get());

        consumer.stop();
        val closedWithTimeout = withDefault(
            closedResponse,
            Boolean.FALSE,
            FAILURE_TIMEOUT);


        assertTrue(closedWithTimeout.get());




    }

    private WebSocketListener getListener(
        CompletableFuture<Boolean> subscribed,
        CompletableFuture<Boolean> closed) {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket websocket) {}
            @Override
            public void onClose(WebSocket websocket, int code, String reason) {
                closed.complete(Boolean.TRUE);
            }
            @Override
            public void onError(Throwable t) {
                subscribed.complete(Boolean.FALSE);
                closed.complete(Boolean.FALSE);
            }
            @Override
            public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                subscribed.complete(Boolean.TRUE);
            }
        };
    }

}