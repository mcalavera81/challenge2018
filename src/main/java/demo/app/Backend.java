package demo.app;

import demo.order.business.state.OrderBook;
import demo.trade.business.state.RecentTradesLog;

public interface Backend {
    void start();

    boolean isRunning();

    void stop();

    RecentTradesLog getRecentTradesLog();

    OrderBook getOrderBook();
}
