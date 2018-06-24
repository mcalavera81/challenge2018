package demo.trade.business.state;

import java.util.List;

public interface RecentTradesLog {
    List<Trade> getRecentTrades(int maxTrades);
}
