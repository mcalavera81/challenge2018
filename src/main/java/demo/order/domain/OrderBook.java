package demo.order.domain;

import demo.order.service.websocket.DiffOrdersBatch;

import java.util.List;

public interface OrderBook {

    long getCurrentSequenceId();

    void update(DiffOrdersBatch batch);

    void clearAllAndLoad(OrderBookSnapshot snapshot);

    List<Order.Ask> getAsks(int... n);

    List<Order.Bid> getBids(int... n);
}
