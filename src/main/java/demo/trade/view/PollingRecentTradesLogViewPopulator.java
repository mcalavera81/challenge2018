package demo.trade.view;

import demo.support.thread.ThreadRunner;
import demo.trade.business.state.RecentTradesLog;
import demo.trade.business.state.Trade;
import demo.trade.view.TradeTableRow.TradeRowType;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static demo.support.helpers.TransformHelpers.getStackTrace;
import static demo.trade.business.state.Trade.TradeType.SELL;
import static demo.trade.view.TradeTableRow.TradeRowType.SIMULATED_BUY;
import static demo.trade.view.TradeTableRow.TradeRowType.SIMULATED_SELL;

@Slf4j
public class PollingRecentTradesLogViewPopulator
    extends Task<ObservableList<TradeTableRow>>
    implements ThreadRunner, TradeViewPopulator {

    private final ListProperty<TradeTableRow> tradeRowsProp;


    private final Integer pollIntervalMs;
    private final Integer maxTrades;
    private Thread tradesThread;
    private final RecentTradesLog tradesLog;


    public PollingRecentTradesLogViewPopulator(
        @NonNull Integer pollIntervalMs,
        @NonNull  Integer maxTrades,
        @NonNull RecentTradesLog tradesLog) {

        this.pollIntervalMs = pollIntervalMs;
        this.maxTrades = maxTrades;
        this.tradesLog = tradesLog;

        ObservableList<TradeTableRow> tradeRows = FXCollections.observableArrayList(new ArrayList<>());
        tradeRowsProp = new SimpleListProperty<>(tradeRows);
    }

    @Override
    public void start() {
        log.info("Starting Trades Table Updater...");
        tradesThread = new Thread(this);
        tradesThread.setDaemon(true);
        tradesThread.start();
    }

    @Override
    public void stop() {
        log.info("Stopping Trades Table Updater...");
        tradesThread = null;
    }


    @Override
    protected ObservableList<TradeTableRow> call() {
        Thread thisThread = Thread.currentThread();

        try {
            while (tradesThread == thisThread) {

                updateTradeRows();

                Thread.sleep(this.pollIntervalMs);
            }
        } catch (Exception e) {
            log.error("Error on Trades Table Updater: {}", getStackTrace(e));
            throw new RuntimeException(e);
        }

        return null;
    }

    private void updateTradeRows() {
        List<Trade> recentTrades = tradesLog.getRecentTrades(this.maxTrades);
        List<TradeTableRow> tableRows = fromTradesToTableRows(recentTrades);
        //getTradeProp().clear();
        getTradeProp().setAll(tableRows);
    }

    private List<TradeTableRow> fromTradesToTableRows(List<Trade> recentTrades) {
        List<TradeTableRow> rows = new LinkedList<>();


        for (int index=0; index < recentTrades.size() ; index++) {
            final Trade trade = recentTrades.get(index);
            TradeRowType type = resolveRowType(recentTrades, index, trade);
            rows.add(TradeTableRow.build(trade,type));
        }
        return rows;
    }

    private TradeRowType resolveRowType(List<Trade> recentTrades, int index, Trade trade) {
        TradeRowType type;
        if(index+1 >= recentTrades.size()) return TradeRowType.UNDEFINED;
        val previousTrade = recentTrades.get(index+1);

        if(trade.isSimulated()){
            type = trade.getType()== SELL?
                SIMULATED_SELL:SIMULATED_BUY;

        }else if (trade.getPrice().compareTo(
            previousTrade.getPrice()) > 0 ){
            type = TradeRowType.UP_TICK;

        }else  if (trade.getPrice().compareTo(
            previousTrade.getPrice()) <0){
            type = TradeRowType.DOWN_TICK;

        }else{
            type = TradeRowType.ZERO_TICK;
        }
        return type;
    }

    @Override
    public ListProperty<TradeTableRow> getTradeProp() {
        return tradeRowsProp;
    }
}
