package demo.trade.business.state;

import demo.trade.business.algorithm.TradingAlgorithm;
import demo.trade.source.poller.TradesSource;
import demo.trade.source.dto.TradesBatch;
import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        tradingAlgorithm.run(queue);
        return queue.getLatest(maxTrades);
    }

    private void processTradesBatch(TradesBatch batch) {
        val trades = batch.getTrades();

        new LinkedList<>(trades)
            .descendingIterator()
            .forEachRemaining(this::processTrade);

    }

    private void processTrade(Trade trade){
        if (trade.getId() > maxId) {
            queue.add(trade);
            maxId = trade.getId();
        }
    }


    public static class LimitedSizeRecentElementsQueue<K> extends LinkedList<K> {

        private int maxSize;


        public LimitedSizeRecentElementsQueue(int size) {
            this.maxSize = size;
        }

        public boolean add(K elem) {
            boolean r = super.offerFirst(elem);
            removeOldests();
            return r;
        }

        private void removeOldests() {
            if (size() > maxSize) {
                removeRange(maxSize, size());
            }
        }

        public List<K> getLatest(int latest) {
            return new ArrayList<>(subList(0, Math.min(latest, size())));
        }

    }
}
