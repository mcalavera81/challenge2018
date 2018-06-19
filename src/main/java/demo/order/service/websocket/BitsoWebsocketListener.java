package demo.order.service.websocket;

import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class BitsoWebsocketListener implements WebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(BitsoWebsocketListener.class);
    private final OrderBuffer buffer;


    public BitsoWebsocketListener(OrderBuffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void onOpen(WebSocket websocket) {
        log.info(format("opened connection to %s", websocket.getRemoteAddress()));
    }

    @Override
    public void onClose(WebSocket websocket, int code, String reason) {
        log.info(format("Connection closed: %s", reason));
    }

    @Override
    public void onError(Throwable t) {
        log.error(format("Error! %s", t));
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
