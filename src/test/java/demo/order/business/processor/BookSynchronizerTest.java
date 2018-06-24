package demo.order.business.processor;

import demo.order.business.state.SyncHashMapOrderBook;
import demo.order.source.poller.client.OrderBookSource;
import demo.order.source.poller.dto.OrderBookSnapshot;
import demo.order.source.stream.client.OrderBuffer;
import demo.shared.parser.UtilParser;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import static demo.TestUtils.*;
import static demo.order.source.TestParserOrderUtils.*;
import static demo.order.source.stream.dto.DiffOrder.OrderType.BUY;
import static demo.order.source.stream.dto.DiffOrder.OrderType.SELL;
import static demo.shared.formatter.UtilFormatter.orderDateFormat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;

public class BookSynchronizerTest {


    private SyncHashMapOrderBook book;
    private OrderBuffer buffer;
    private BookSynchronizer updater;
    private OrderBookSource bookSource;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        book = new SyncHashMapOrderBook();
        buffer = new OrderBuffer(10);
        bookSource = mockOrderBookSource(0);
        updater = new BookSynchronizer(buffer, book, bookSource);
    }

    @Test
    public void trigger_book_load() {
        // -------------- GIVEN --------------
        triggerFirstOrderBook();

        // -------------- WHEN --------------
        updater.processOrderStream();

        // -------------- THEN --------------
        int orderSourceCalls = 1, sequenceId = 0, bids = 2, asks = 2;
        assertBook(orderSourceCalls, sequenceId, bids, asks);


    }

    @Test
    public void replaying_two_bids() {
        // -------------- GIVEN --------------
        triggerFirstOrderBook();


        JSONObject order1 = orderBatch(BUY, 1, "new1");
        JSONObject order2 = orderBatch(BUY, 2, "new2");

        // -------------- WHEN --------------

        buffer.addMessage(order1.toString());
        buffer.addMessage(order2.toString());
        updater.processOrderStream();

        // -------------- THEN --------------
        int orderSourceCalls = 1, sequenceId = 2, bids = 4, asks = 2;
        assertBook(orderSourceCalls, sequenceId, bids, asks);


    }

    @Test
    public void replaying_and_updating_existing_bid() {

        triggerFirstOrderBook();

        JSONObject oldBid = orderBatch(BUY, 1, "1");

        // -------------- WHEN --------------

        buffer.addMessage(oldBid.toString());
        updater.processOrderStream();

        // -------------- THEN --------------


        int orderSourceCalls = 1, sequenceId = 1, bids = 2, asks = 2;
        assertBook(orderSourceCalls, sequenceId, bids, asks);

    }

    @Test
    public void replaying_bid_wrong_sequence() {

        triggerFirstOrderBook();

        JSONObject newBid = orderBatch(BUY, 2, "new");

        // -------------- WHEN --------------

        buffer.addMessage(newBid.toString());
        updater.processOrderStream();

        // -------------- THEN --------------

        int orderSourceCalls = 1, sequenceId = 0, bids = 2, asks = 2;
        assertBook(orderSourceCalls, sequenceId, bids, asks);
    }

    @Test
    public void replaying_old_and_new_bids_() {

        triggerFirstOrderBook();

        // -------------- WHEN --------------

        buffer.addMessage(orderBatch(BUY, -1, "new1").toString());
        buffer.addMessage(orderBatch(SELL, 1, "new2").toString());
        updater.processOrderStream();

        // -------------- THEN --------------


        int orderSourceCalls = 1, sequenceId = 1, bids = 2, asks = 3;
        assertBook(orderSourceCalls, sequenceId, bids, asks);


    }

    @Test
    public void update_book_with_new_bid() {

        triggerFirstOrderBook();

        // -------------- WHEN --------------

        updater.processOrderStream();
        buffer.addMessage(orderBatch(BUY, 1, "new2").toString());
        updater.processOrderStream();

        // -------------- THEN --------------

        int orderSourceCalls = 1, sequenceId = 1, bids = 3, asks = 2;
        assertBook(orderSourceCalls, sequenceId, bids, asks);


    }

    @Test
    public void bid_wrong_sequence_forces_reload() {

        triggerFirstOrderBook();

        // -------------- WHEN --------------

        updater.processOrderStream();
        buffer.addMessage(orderBatch(BUY, 2, "new2").toString());
        updater.processOrderStream();

        // -------------- THEN --------------

        int orderSourceCalls = 2, sequenceId = 0, numberBids = 2, numberAsks = 2;
        assertBook(orderSourceCalls, sequenceId, numberBids, numberAsks);

    }

    @Test
    public void bid_with_wrong_sequence_forces_reload_and_replay() {

        bookSource = mockOrderBookSource(0,5);
        updater = new BookSynchronizer(buffer, book, bookSource);

        triggerFirstOrderBook();

        // -------------- WHEN --------------

        updater.processOrderStream();
        buffer.addMessage(orderBatch(BUY, 1, "new").toString());
        updater.processOrderStream();
        buffer.addMessage(orderBatch(BUY, 4, "new2").toString());
        buffer.addMessage(orderBatch(BUY, 6, "new3").toString());
        updater.processOrderStream();

        // -------------- THEN --------------

        int orderSourceCalls = 2, sequenceId = 6, numberBids = 3, numberAsks
            = 2;
        assertBook(orderSourceCalls, sequenceId, numberBids, numberAsks);

    }


    private void assertBook(int orderSourceCalls, int currentSequence, int numberBids, int numberAsks) {
        Mockito.verify(bookSource, Mockito.times(orderSourceCalls)).getOrderBook();
        assertEquals(currentSequence, book.getCurrentSequenceId());
        assertThat(book.getBids(), hasSize(numberBids));
        assertThat(book.getAsks(), hasSize(numberAsks));
    }


    private void triggerFirstOrderBook() {
        String diffOrder = loadString("book/diff_order_batch.json").get();

        // -------------- WHEN --------------

        buffer.addMessage(diffOrder);
    }

    private OrderBookSource mockOrderBookSource(long... sequence) {

        List<JSONObject> bids = Arrays.asList(
            order(randPrice(), randAmount(), "1"),
            order(randPrice(), randAmount(), "2"));
        List<JSONObject> asks = Arrays.asList(
            order(randPrice(), randAmount(), "3"),
            order(randPrice(), randAmount(), "4"));


        List<JSONObject> responses =
            Stream.ofAll(sequence).map($ ->
                book(
                    $,
                    orderDateFormat(UtilParser.now()),
                    Option.of(bids),
                    Option.of(asks))).toJavaList();

        OrderBookSource bookSource = Mockito.mock(OrderBookSource.class);
        Mockito.when(bookSource.getOrderBook()).thenAnswer(new Answer<Object>() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) {
                return OrderBookSnapshot.build(responses.get(count++));
            }
        });

        return bookSource;
    }
}