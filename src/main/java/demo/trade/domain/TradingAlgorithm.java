package demo.trade.domain;

import io.vavr.control.Option;

public interface TradingAlgorithm {
    Option<Trade> run(Trade[] history);
}
