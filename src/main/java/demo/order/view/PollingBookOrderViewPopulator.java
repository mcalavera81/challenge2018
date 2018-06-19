package demo.order.view;

import demo.order.domain.OrderBook;
import demo.order.view.OrderView.AskView;
import demo.order.view.OrderView.BidView;
import demo.shared.service.ThreadRunner;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

import static demo.shared.parser.UtilParser.getStackTrace;
import static io.vavr.collection.Stream.ofAll;

@Slf4j
public class PollingBookOrderViewPopulator
    extends Task<ObservableList<OrderView>> implements ThreadRunner, OrderViewPopulator {

    private final Integer maxOrders;
    private Thread bookThread;

    private final ObservableList<BidView> bids;
    private final ListProperty<BidView> bidsProp;

    private final ObservableList<AskView> asks;
    private final ListProperty<AskView> asksProp;

    private final OrderBook book;

    public PollingBookOrderViewPopulator(
        @NonNull  Integer maxOrders,
        @NonNull OrderBook book){

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
    protected ObservableList<OrderView> call() {
        Thread thisThread = Thread.currentThread();

        try{
            while(bookThread == thisThread){

                getAsksProp().setAll(
                    ofAll(book.getAsks(maxOrders)).map(AskView::build).asJava()
                );

                getBidsProp().setAll(
                    ofAll(book.getBids(maxOrders)).map(BidView::build).asJava()
                );

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("Error on Order Book Table Updater: {}", getStackTrace(e));
            throw new RuntimeException(e);
        }

        return null;
    }


    @Override
    public ListProperty<BidView> getBidsProp() {
        return bidsProp;
    }

    @Override
    public ListProperty<AskView> getAsksProp() {
        return asksProp;
    }
}
