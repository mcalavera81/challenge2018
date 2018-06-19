package demo.trade.domain;

import io.vavr.control.Option;
import lombok.NonNull;

public class ContrarianTradingStrategySimulator implements TradingAlgorithm {

    private Integer upTicks;
    private Integer downTicks;

    public ContrarianTradingStrategySimulator(
        @NonNull Integer upTicks,
        @NonNull Integer downTicks) {
        this.upTicks = upTicks;
        this.downTicks = downTicks;
    }

    @Override
    public Option<Trade> run(Trade[] history) {
        return Option.none();
    }
}

