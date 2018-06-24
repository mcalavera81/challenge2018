package demo.order.source.stream.client;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

@Slf4j
public class BitsoWebsocketListener implements WebSocketListener {

    private final OrderBuffer buffer;


    public BitsoWebsocketListener(OrderBuffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void onOpen(WebSocket websocket) {
        log.info("opened connection to {}", websocket.getRemoteAddress());
    }

    @Override
    public void onClose(WebSocket websocket, int code, String reason) {
        log.info("Connection closed: {}", reason);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error! {}", t);
    }

    @Override
    public void onTextFrame(String payload, boolean finalFragment, int rsv) {
        try{
            log.debug("Message on diff-orders: {}", payload);
            buffer.addMessage(payload);
        }catch (Exception e){
            log.error("Websocket. Error unmarshalling json {}",e);
        }

    }
}
