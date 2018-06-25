package demo.order.source.stream.client;

import demo.support.thread.ThreadRunner;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.json.JSONObject;

import static demo.app.Constants.BitsoBook.BTC_MXN;
import static demo.support.helpers.TransformHelpers.getStackTrace;

@Slf4j
public class DiffOrderStreamConsumer implements ThreadRunner {


    private final AsyncHttpClient client;
    private final String baseUri;
    private final WebSocketListener listener;

    public DiffOrderStreamConsumer(
        @NonNull String baseUri,
        @NonNull AsyncHttpClient client,
        @NonNull WebSocketListener listener) {

        this.client = client;
        this.baseUri = baseUri;
        this.listener = listener;
    }

    private WebSocket webSocket;

    @Override
    public void start(){
        log.info("Starting Order Stream Consumer...");
        connect();
        subscribeToDiffOrders();
    }

    @Override
    public void stop() {
        if (webSocket != null) {
            log.info("Stopping Order Stream Consumer...");
            webSocket.sendCloseFrame().awaitUninterruptibly();
        }

    }

    private void subscribeToDiffOrders() {
        JSONObject subscriptionCommand = new JSONObject()
            .put("action", "subscribe")
            .put("book", BTC_MXN.id())
            .put("type", "diff-orders");
        this.webSocket.sendTextFrame(subscriptionCommand.toString());
    }

    private void connect() {
        try {
            this.webSocket = client.prepareGet(baseUri)
                .execute(
                    new WebSocketUpgradeHandler
                        .Builder()
                        .addWebSocketListener(listener).build()
                ).get();
        } catch (Exception e) {
            log.error(
                "Error opening websocket: {}", getStackTrace(e));
            throw new RuntimeException(e);
        }
    }
}
