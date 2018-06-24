package demo.order.source.poller.client;

import demo.order.source.poller.dto.OrderBookSnapshot;
import io.vavr.control.Try;

public interface OrderBookSource {

    Try<OrderBookSnapshot> getOrderBook();


}
