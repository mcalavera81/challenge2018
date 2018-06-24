package demo.trade.business.algorithm;

import demo.trade.business.state.Trade;

import java.util.List;

public interface TradingAlgorithm {
    void run(List<Trade> history);
}
