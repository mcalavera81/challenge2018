package demo.order.view;

import demo.order.business.state.Order;
import demo.order.business.state.OrderBook;
import demo.shared.service.ThreadRunner;
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

import static demo.shared.parser.UtilParser.getStackTrace;
import static io.vavr.collection.Stream.ofAll;

@Slf4j
public class PollingOrderBookViewPopulator
    extends Task<ObservableList<OrderTableRow>> implements ThreadRunner, OrderViewPopulator {

    private final Integer pollIntervalMs;
    private final Integer maxOrders;
    private Thread bookThread;

    private final ObservableList<OrderTableRow> bids;
    private final ListProperty<OrderTableRow> bidsProp;

    private final ObservableList<OrderTableRow> asks;
    private final ListProperty<OrderTableRow> asksProp;

    private final OrderBook book;

    public PollingOrderBookViewPopulator(
        @NonNull Integer pollIntervalMs,
        @NonNull  Integer maxOrders,
        @NonNull OrderBook book){

        this.pollIntervalMs = pollIntervalMs;
        this.maxOrders = maxOrders;
        this.book = book;

        this.bids = FXCollections.observableArrayList(new ArrayList<>());
        this.bidsProp = new SimpleListProperty<>(bids);

        this.asks = FXCollections.observableArrayList(new ArrayList<>());
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

                final List<Order.Ask> asks = book.getAsks(maxOrders);

                List<OrderTableRow> askRows = ofAll(asks)
                    .foldLeft(
                        Tuple.of(new LinkedList<OrderTableRow>(), BigDecimal.ZERO),
                        (Tuple2<List<OrderTableRow>, BigDecimal> acc, Order.Ask ask) -> {
                            val sum = ask.getAmount().add(acc._2);
                            acc._1().add(OrderTableRow.build(ask, sum));
                            return Tuple.of(acc._1, sum);

                        })._1;

                getAsksProp().setAll(askRows);

                final List<Order.Bid> bids = book.getBids(maxOrders);

                List<OrderTableRow> bidsRows = ofAll(bids)
                    .foldLeft(
                        Tuple.of(new LinkedList<OrderTableRow>(), BigDecimal.ZERO),
                        (Tuple2<List<OrderTableRow>, BigDecimal> acc, Order.Bid
                            bid) -> {
                            val sum = bid.getAmount().add(acc._2);
                            acc._1().add(OrderTableRow.build(bid, sum));
                            return Tuple.of(acc._1, sum);

                        })._1;

                getBidsProp().setAll(bidsRows);

                Thread.sleep(this.pollIntervalMs);
            }
        } catch (Exception e) {
            log.error("Error on Order Book Table Updater: {}", getStackTrace(e));
            throw new RuntimeException(e);
        }

        return null;
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
