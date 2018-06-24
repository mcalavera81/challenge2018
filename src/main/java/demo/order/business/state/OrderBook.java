package demo.order.business.state;

import demo.order.source.stream.dto.DiffOrdersBatch;
import demo.order.source.poller.dto.OrderBookSnapshot;

import java.util.List;

public interface OrderBook {

    long getCurrentSequenceId();

    void update(DiffOrdersBatch batch);

    void clearAllAndLoad(OrderBookSnapshot snapshot);

    List<Order.Ask> getAsks(int... n);

    List<Order.Bid> getBids(int... n);
}
