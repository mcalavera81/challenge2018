package demo.trade.domain;

import java.util.List;

public interface RecentTradesLog {
    List<Trade> getRecentTrades(int maxTrades);
}
