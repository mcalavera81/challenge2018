package demo.order.view;

import demo.order.business.state.Order;
import demo.order.business.state.OrderBook;
import demo.support.thread.ThreadRunner;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static demo.support.helpers.TransformHelpers.getStackTrace;
import static io.vavr.collection.Stream.ofAll;

@Slf4j
public class PollingOrderBookViewPopulator
    extends Task<ObservableList<OrderTableRow>> implements ThreadRunner, OrderViewPopulator {

    private final Integer pollIntervalMs;
    private final Integer maxOrders;
    private Thread bookThread;

    private final ListProperty<OrderTableRow> bidsProp;
    private final ListProperty<OrderTableRow> asksProp;

    private final OrderBook book;

    public PollingOrderBookViewPopulator(
        @NonNull Integer pollIntervalMs,
        @NonNull  Integer maxOrders,
        @NonNull OrderBook book){

        this.pollIntervalMs = pollIntervalMs;
        this.maxOrders = maxOrders;
        this.book = book;

        ObservableList<OrderTableRow> bids = FXCollections.observableArrayList(new ArrayList<>());
        this.bidsProp = new SimpleListProperty<>(bids);

        ObservableList<OrderTableRow> asks = FXCollections.observableArrayList(new ArrayList<>());
        this.asksProp = new SimpleListProperty<>(asks);
    }

    @Override
    public void start() {
        log.info("Starting Order Book View Updater...");
        bookThread = new Thread(this);
        bookThread.setDaemon(true);
        bookThread.start();


    }

    @Override
    public void stop() {
        log.info("Stopping Order Book View Updater...");
        bookThread = null;
    }

    @Override
    protected ObservableList<OrderTableRow> call() {
        Thread thisThread = Thread.currentThread();

        try{
            while(bookThread == thisThread){
                updateAskRows();
                updateBidRows();
                Thread.sleep(this.pollIntervalMs);
            }
        } catch (Exception e) {
            log.error("Error on Order Book Table Updater: {}", getStackTrace(e));
            throw new RuntimeException(e);
        }

        return null;
    }

    private void updateBidRows() {
        final List<Order.Bid> bids = book.getBids(maxOrders);
        List<OrderTableRow> bidsRows = mapOrdersToRows(bids);
        getBidsProp().setAll(bidsRows);
    }

    private void updateAskRows() {
        final List<Order.Ask> asks = book.getAsks(maxOrders);
        List<OrderTableRow> askRows = mapOrdersToRows(asks);
        getAsksProp().setAll(askRows);
    }

    private List<OrderTableRow> mapOrdersToRows(List<? extends Order> bids) {
        return ofAll(bids)
            .foldLeft(
                Tuple.of(new LinkedList<>(), BigDecimal.ZERO),
                (Tuple2<List<OrderTableRow>, BigDecimal> acc, Order bid)-> {
                    val sum = bid.getAmount().add(acc._2);
                    acc._1.add(OrderTableRow.build(bid, sum));
                    return Tuple.of(acc._1, sum);

                })._1;
    }



    @Override
    public ListProperty<OrderTableRow> getBidsProp() {
        return bidsProp;
    }

    @Override
    public ListProperty<OrderTableRow> getAsksProp() {
        return asksProp;
    }
}
