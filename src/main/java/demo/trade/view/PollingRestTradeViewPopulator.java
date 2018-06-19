package demo.trade.view;

import demo.trade.domain.RecentTradesLog;
import demo.shared.service.ThreadRunner;
import demo.trade.domain.Trade;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static demo.order.parser.UtilParser.getStackTrace;

@Slf4j
public class PollingRestTradeViewPopulator
    extends Task<ObservableList<TradeTableRow>>
    implements ThreadRunner, TradeViewPopulator {

    private final ObservableList<TradeTableRow> tradeRows;
    private final ListProperty<TradeTableRow> tradeRowsProp;


    private final Integer maxTrades;
    private Thread tradesThread;
    private final RecentTradesLog tradesLog;


    public PollingRestTradeViewPopulator(
        @NonNull  Integer maxTrades,
        @NonNull RecentTradesLog tradesLog) {
        this.maxTrades = maxTrades;
        this.tradesLog = tradesLog;

        this.tradeRows = FXCollections.observableArrayList(new ArrayList<>());
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

                List<Trade> recentTrades = tradesLog.getRecentTrades(this.maxTrades);

                List<TradeTableRow> rows = recentTrades.stream()
                    .map(TradeTableRow::build).collect(Collectors.toList());

                //getTradeProp().clear();
                getTradeProp().setAll(rows);

                Thread.sleep(3000);
            }
        } catch (Exception e) {
            log.error("Error on Trades Table Updater: {}", getStackTrace(e));
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public ListProperty<TradeTableRow> getTradeProp() {
        return tradeRowsProp;
    }
}
