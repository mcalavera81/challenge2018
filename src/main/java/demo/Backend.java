package demo;

import demo.order.domain.OrderBook;
import demo.trade.domain.RecentTradesLog;

public interface Backend {
    void start();

    boolean isRunning();

    void stop();

    RecentTradesLog getRecentTradesLog();

    OrderBook getOrderBook();
}
