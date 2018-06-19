package demo.trade.domain;

import demo.order.domain.LimitedSizeRecentElementsQueue;
import demo.trade.service.TradesSource;
import lombok.NonNull;
import lombok.val;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LatestTradesContainer implements RecentTradesLog {

    private final Integer restDefaultLimit;
    private final TradesSource tradesSource;
    private final TradingAlgorithm tradingAlgorithm;
    private final LimitedSizeRecentElementsQueue<Trade> queue;

    private Long maxId;

    public LatestTradesContainer(
        @NonNull TradesSource tradesSource,
        @NonNull Integer cacheSize,
        @NonNull Integer restDefaultLimit,
        @NonNull TradingAlgorithm tradingAlgorithm
    ) {
        this.restDefaultLimit = restDefaultLimit;
        this.tradesSource = tradesSource;
        this.queue = new LimitedSizeRecentElementsQueue<>(cacheSize);
        this.maxId = Long.MIN_VALUE;
        this.tradingAlgorithm = tradingAlgorithm;
    }


    public int size() {
        return queue.size();
    }


    @Override
    public List<Trade> getRecentTrades(int maxTrades) {
        val recentTrades = tradesSource.getRecentTradesSortDesc(restDefaultLimit);
        recentTrades.onSuccess(this::processTradesBatch);
        return queue.getLatest(maxTrades);
    }

    private void processTradesBatch(TradesBatch batch) {
        val trades = batch.getTrades();

        Stream
            .of(trades)
            .collect(Collectors.toCollection(LinkedList::new))
            .descendingIterator()
            .forEachRemaining(this::processTrade);

    }

    private void processTrade(Trade trade){
        if (trade.getId() > maxId) {
            queue.add(trade);
            maxId = trade.getId();
        }
    }


}
