package demo.order.business.state;

import demo.order.TestOrderHelpers;
import demo.order.source.poller.dto.OrderBookSnapshot;
import demo.order.source.stream.dto.DiffOrder.OrderType;
import demo.support.helpers.DateTimeHelpers;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static demo.TestHelpers.randAmount;
import static demo.TestHelpers.randPrice;
import static demo.order.TestOrderHelpers.*;
import static demo.order.source.poller.parser.OrderBookSnaphsotParser.parseOrderBook;
import static demo.order.source.stream.dto.DiffOrder.OrderType.BUY;
import static demo.order.source.stream.dto.DiffOrder.OrderType.SELL;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class SyncHashMapOrderBookTest {


    private SyncHashMapOrderBook book;

    private long  sequence;


    @Before
    public void init() {

        book = new SyncHashMapOrderBook();
        sequence =0;
    }

    @Test
    public void empty_book_has_no_bids_or_asks() {
        assertThat(book.getAsks(), Matchers.empty());
        assertBidsAndAsks(0, 0);
    }
    @Test
    public void overwriting_same_ask() {

        assertBidsAndAsks(0, 0);

        String orderId = "orderId";
        newBookOrder(orderId, SELL, 10);
        newBookOrder(orderId, SELL, 12);

        assertBidsAndAsks(0, 1);
    }

    @Test
    public void load_book() {

        assertBidsAndAsks(0, 0);
        loadBookSnapshot(1L);
        assertBidsAndAsks(2, 2);
    }

    @Test
    public void book_not_updated_with_lower_sequence_id(){
        loadBookSnapshot(1L);

        this.sequence =3;
        newBookOrder("orderId1", SELL, 10);
        this.sequence =2;
        newBookOrder("orderId2", SELL, 10);

        Assert.assertEquals(book.getCurrentSequenceId(), 3);
        Assert.assertEquals(book.getAsks().size(), 3);


    }

    @Test
    public void book_sequence_is_updated(){

        loadBookSnapshot(1L);
        final String orderId = "orderId";

        Assert.assertEquals(1, book.getCurrentSequenceId());
        newBookOrder(orderId, SELL, 10);
        Assert.assertEquals(2, book.getCurrentSequenceId());
        newBookOrder(orderId, BUY, 10);
        Assert.assertEquals(3, book.getCurrentSequenceId());

    }

    @Test
    public void cancel_bid() {

        loadBookSnapshot(1L);

        assertBidsAndAsks(2, 2);

        cancelOrder(BUY);

        assertBidsAndAsks(1, 2);

    }

    @Test
    public void cancel_ask() {


        loadBookSnapshot(1L);

        assertBidsAndAsks(2, 2);

        cancelOrder(SELL);

        assertBidsAndAsks(2, 1);

    }


    @Test
    public void load_book_preloaded_with_orders() {

        newBookOrders(SELL, 10);

        assertBidsAndAsks(0, 1);
        loadBookSnapshot(1L);

        assertBidsAndAsks(2, 2);

    }


    @Test
    public void get_best_asks_sorted() {
        newBookOrders(SELL, 10,30,20,5);

        assertBidsAndAsks(0, 4);

        assertThat(
            Stream.ofAll(book.getAsks()).map($ -> $.getPrice().doubleValue()),
            contains(5.0, 10.0, 20.0, 30.0));

    }



    @Test
    public void get_best_bids_sorted() {
        newBookOrders(BUY, 10,30,20,5);

        assertBidsAndAsks(4, 0);

        assertThat(
            Stream.ofAll(book.getBids()).map($ -> $.getPrice().doubleValue()),
            contains(30.0, 20.0, 10.0, 5.0));

    }

    private void cancelOrder(OrderType type) {
        switch (type){
            case BUY:
                book.update(batchCancelled(this.sequence++, "ID_1", BUY));
                break;
            case SELL:
                book.update(batchCancelled(this.sequence++, "ID_3", SELL));
                break;
        }

    }

    private void loadBookSnapshot(long sequenceId){
        this.sequence = sequenceId +1;
        OrderBookSnapshot bookSnapshot = newBookSnapshot(
            sequenceId,
            Arrays.asList("ID_1","ID_2"),
            Arrays.asList("ID_3","ID_4"));
        book.clearAllAndLoad(bookSnapshot);
    }

    public void newBookOrder(String orderId, OrderType type, double
        price){
        book.update(batch(this.sequence++, orderId, type, price));
    }

    private void newBookOrders(OrderType orderType, double... prices){
        for (double price : prices) {
            book.update(batch(this.sequence++, "" + this.sequence,
                orderType, price));
        }
    }



    private OrderBookSnapshot newBookSnapshot(long sequence, List<String> bidsId,
                                              List<String> asksId) {

        List<JSONObject> bids = Stream
            .ofAll(bidsId)
            .map(id -> order(randPrice(), randAmount(), id))
            .toJavaList();

        List<JSONObject> asks = Stream
            .ofAll(asksId)
            .map(id -> order(randPrice(), randAmount(), id))
            .toJavaList();


        return parseOrderBook(TestOrderHelpers
            .book(
                sequence,
                DateTimeHelpers.orderDateFormat(DateTimeHelpers.now()),
                Option.of(bids),
                Option.of(asks)
            )).get();
    }

    private void assertBidsAndAsks(
        int expectedBids,
        int expectedAsks){

        Assert.assertEquals(expectedBids, book.getBids().size());
        Assert.assertEquals(expectedAsks, book.getAsks().size());

    }


}