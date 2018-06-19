package demo.order.domain;

import demo.order.parser.TestOrderUtils;
import demo.order.parser.UtilParser;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static demo.TestUtils.*;
import static demo.order.parser.OrderBookSnaphsotParser.parseOrderBook;
import static demo.order.parser.TestOrderUtils.*;
import static demo.order.service.websocket.DiffOrder.OrderType.BUY;
import static demo.order.service.websocket.DiffOrder.OrderType.SELL;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class SyncHashMapOrderBookTest {


    private SyncHashMapOrderBook book;

    @Before
    public void init() {

        book = new SyncHashMapOrderBook();
    }

    @Test
    public void empty_book_has_no_bids_or_asks() {
        assertThat(book.getAsks(), Matchers.empty());
        assertBidsAndAsks(0, 0);
    }
    @Test
    public void overwriting_same_ask() {

        assertBidsAndAsks(0, 0);

        long sequence = 1;
        String orderId = "orderId";
        book.update(batch(sequence++, orderId, SELL, 10));
        book.update(batch(sequence, orderId, SELL, 12));

        assertBidsAndAsks(0, 1);
    }

    @Test
    public void load_book() {
        OrderBookSnapshot bookSnapshot = newBookSnapshot(1L,
            Arrays.asList("1","2"), Arrays.asList("3","4"));

        assertBidsAndAsks(0, 0);

        book.clearAllAndLoad(bookSnapshot);

        assertBidsAndAsks(2, 2);
    }

    @Test
    public void book_not_updated_with_lower_sequence_id(){
        OrderBookSnapshot bookSnapshot = newBookSnapshot(1L,
            Arrays.asList("1","2"), Arrays.asList("3","4"));
        book.clearAllAndLoad(bookSnapshot);
        book.update(batch(3, "orderId1", SELL, 20));
        book.update(batch(2, "orderId2", SELL, 20));

        Assert.assertEquals(book.getCurrentSequenceId(), 3);
        Assert.assertEquals(book.getAsks().size(), 3);


    }

    @Test
    public void book_sequence_is_updated(){

        OrderBookSnapshot bookSnapshot = newBookSnapshot(1L,
            Arrays.asList("1","2"), Arrays.asList("3","4"));
        book.clearAllAndLoad(bookSnapshot);

        Assert.assertEquals(1, book.getCurrentSequenceId());
        book.update(batch(2, "orderId", SELL, 10));
        Assert.assertEquals(2, book.getCurrentSequenceId());
        book.update(batch(3, "orderId", BUY, 10));
        Assert.assertEquals(3, book.getCurrentSequenceId());

    }

    @Test
    public void cancel_bid() {
        OrderBookSnapshot bookSnapshot = newBookSnapshot(1L,
            Arrays.asList("1","2"), Arrays.asList("3","4"));

        book.clearAllAndLoad(bookSnapshot);

        assertBidsAndAsks(2, 2);

        book.update(batchCancelled(2L, "1", BUY));

        assertBidsAndAsks(1, 2);

    }

    @Test
    public void cancel_ask() {
        OrderBookSnapshot bookSnapshot = newBookSnapshot(1L,
            Arrays.asList("1","2"), Arrays.asList("3","4"));


        book.clearAllAndLoad(bookSnapshot);

        assertBidsAndAsks(2, 2);

        book.update(batchCancelled(2L, "3", SELL));

        assertBidsAndAsks(2, 1);

    }


    @Test
    public void load_book_preloaded_with_orders() {

        OrderBookSnapshot bookSnapshot = newBookSnapshot(1L,
            Arrays.asList("1","2"), Arrays.asList("3","4"));

        book.update(batch(1, "orderId", SELL, 10));
        assertBidsAndAsks(0, 1);
        book.clearAllAndLoad(bookSnapshot);

        assertBidsAndAsks(2, 2);

    }


    @Test
    public void get_best_asks_sorted() {
        long sequence = 1;
        book.update(batch(sequence++, "" + sequence, SELL, 10));
        book.update(batch(sequence++, "" + sequence, SELL, 30));
        book.update(batch(sequence++, "" + sequence, SELL, 20));
        book.update(batch(sequence++, "" + sequence, SELL, 5));

        assertBidsAndAsks(0, 4);

        assertThat(
            Stream.ofAll(book.getAsks()).map($ -> $.getPrice().doubleValue()),
            contains(5.0, 10.0, 20.0, 30.0));

    }



    @Test
    public void get_best_bids_sorted() {
        long sequence = 1;
        book.update(batch(sequence++, "" + sequence, BUY, 10));
        book.update(batch(sequence++, "" + sequence, BUY, 30));
        book.update(batch(sequence++, "" + sequence, BUY, 20));
        book.update(batch(sequence++, "" + sequence, BUY, 5));

        assertBidsAndAsks(4, 0);


        assertThat(
            Stream.ofAll(book.getBids()).map($ -> $.getPrice().doubleValue()),
            contains(30.0, 20.0, 10.0, 5.0));

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


        return parseOrderBook(TestOrderUtils
            .book(
                sequence,
                UtilParser.orderDateFormat(now()),
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