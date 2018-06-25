package demo.trade.business.algorithm;

import demo.support.helpers.DateTimeHelpers;
import demo.trade.business.state.Trade;
import demo.trade.business.state.Trade.TradeType;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static demo.app.Constants.BitsoBook.BTC_MXN;

public class ContrarianTradingStrategy implements TradingStrategy {

    private Integer upTicksThreshold;
    private Integer downTicksThreshold;
    private long runningId = 0;

    private final Trade.TradeBuilder tradeTemplate;

    public ContrarianTradingStrategy(
        @NonNull Integer upTicksThreshold,
        @NonNull Integer downTicksThreshold) {
        this.upTicksThreshold = upTicksThreshold;
        this.downTicksThreshold = downTicksThreshold;
        tradeTemplate = Trade.builder()
            .source(Trade.TradeSource.SIMULATED)
            .amount(BigDecimal.ONE)
            .book(BTC_MXN.id());
    }

    @Override
    public void run(List<Trade> trades) {
        int firstNotProcessed = findOldestTradeIndexNotProcessed(trades);
        runFromIndex(trades, firstNotProcessed);
    }

    private List<Trade> runFromIndex(List<Trade> history, int
        firstNotProcessed) {
        //List<Trade> history = new LinkedList<>(trades);
        double prevPrice = Double.NaN;

        for (int i= firstNotProcessed,upCount = 0, downCount = 0; i>=0; i--) {

            final Trade t = history.get(i);
            final BigDecimal price = t.getPrice();
            if (isPriceIncreasing(prevPrice, price)) {
                downCount =0;
                upCount++;
                if (isSellTriggered(upCount)) {
                    addSimulatedTrade(history, i, t, TradeType.SELL);
                    upCount =0;

                }
            } else if (isPriceDecreasing(prevPrice, price)) {
                upCount=0;
                downCount++;
                if (isBuyTriggered(downCount)) {
                    addSimulatedTrade(history, i, t, TradeType.BUY);
                    downCount=0;
                }
            }
            prevPrice = price.doubleValue();
        }
        return history;
    }

    private boolean isBuyTriggered(int downCount) {
        return downCount >= downTicksThreshold;
    }

    private boolean isSellTriggered(int upCount) {
        return upCount >= upTicksThreshold;
    }

    private boolean isPriceDecreasing(double prevPrice, BigDecimal price) {
        return price.doubleValue() < prevPrice;
    }

    private boolean isPriceIncreasing(double prevPrice, BigDecimal price) {
        return price.doubleValue() > prevPrice;
    }

    private void addSimulatedTrade(List<Trade> history, int i, Trade t, TradeType type) {
        final Trade sell = tradeTemplate
            .price(t.getPrice())
            .timestamp(DateTimeHelpers.now())
            .id(--runningId)
            .type(type)
            .build();

        history.add(i, sell);
    }

    private int findOldestTradeIndexNotProcessed(List<Trade> history) {
        return IntStream.range(0, history.size())
            .filter(i -> history.get(i).isSimulated())
            .findFirst()
            .orElse(history.size()-1);
    }


}

