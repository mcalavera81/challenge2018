package demo.order.source.poller;

import demo.TestHelpers;
import demo.order.TestOrderHelpers;
import demo.order.source.poller.dto.OrderBookSnapshot;
import demo.order.source.poller.parser.OrderBookSnaphsotParser.ResponseServerException;
import demo.support.helpers.DateTimeHelpers;
import demo.support.helpers.TransformHelpers;
import demo.support.parser.JsonParser.ParserException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static demo.app.Constants.MXN_ERROR;
import static demo.app.Constants.SATOSHI_ERROR;
import static demo.TestHelpers.randSeq;
import static demo.order.TestOrderHelpers.order;
import static junit.framework.TestCase.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrderBookSnaphsotParserTest {


    private String timestamp;

    @Before
    public void init(){
        timestamp = "2018-06-05T17:55:22+00:00";
    }

    @Test
    public void test_build_OrderBook_empty(){
        JSONObject jsonObject = new JSONObject("{}");
        Try<OrderBookSnapshot> ob = OrderBookSnapshot.build(jsonObject);
        assertThat(ob.getCause(), is(instanceOf(ParserException.class)));

    }


    @Test
    public void test_order_book_error() {
        String filename = "order/order_book_error.json";
        Try<JSONObject> json = TestHelpers.loadJson(filename);

        val obTry = OrderBookSnapshot.build(json.get());
        assertTrue(obTry.isFailure());

        assertThat(obTry.getCause(), is(instanceOf(ResponseServerException.class)));

    }

    @Test
    public void test_order_book_ok() {
        String filename = "order/order_book_ok.json";
        Try<JSONObject> json = TestHelpers.loadJson(filename);

        Try<OrderBookSnapshot> obTry = OrderBookSnapshot.build(json.get());
        assertTrue(obTry.isSuccess());
        OrderBookSnapshot ob = obTry.get();
        assertThat(ob.getBids(),arrayWithSize(4));
        assertThat(ob.getAsks(),arrayWithSize(3));
        assertNotNull(ob.getTimestamp());

        assertEquals(ob.getTimestamp(),ob.getBids()[0].getTimestamp());
        assertEquals(ob.getTimestamp(),ob.getAsks()[0].getTimestamp());

    }

    @Test
    public void test_build_OrderEntry__no_bids_no_asks_ok(){

        JSONObject jsonObject = TestOrderHelpers.book(randSeq(), timestamp, Option.of(Collections.EMPTY_LIST),Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> ob = OrderBookSnapshot.build(jsonObject);
        assertTrue(ob.isSuccess());
        assertThat(ob.get().getAsks(), emptyArray());
        assertThat(ob.get().getBids(), emptyArray());

    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_oid(){

        List<JSONObject> bids = Collections.singletonList(order(153663.00, 0.00008714, null));

        JSONObject json = TestOrderHelpers.book(randSeq(),timestamp , Option.of(bids), Option.of(Collections.EMPTY_LIST));

        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"oid\"] not found"));


    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_amount(){

        List<JSONObject> bids = Collections.singletonList(order(153663.00, null, "1ugNZMSlhr6MgHhq"));

        JSONObject json = TestOrderHelpers.book(randSeq(),timestamp , Option.of(bids),Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"amount\"] not found"));

    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_price(){

        List<JSONObject> bids = Collections.singletonList(order(null, 0.00008714, "1ugNZMSlhr6MgHhq"));

        JSONObject json = TestOrderHelpers.book(randSeq(), timestamp, Option.of(bids), Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"price\"] not found"));

    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_bids(){

        JSONObject json = TestOrderHelpers.book(randSeq(),timestamp , Option.none(), Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"bids\"] not found"));
    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_asks(){

        JSONObject json = TestOrderHelpers.book(randSeq(), timestamp, Option.of(Collections
                .EMPTY_LIST), Option.none());
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"asks\"] not found"));
    }

    @Test
    public void test_build_OrderEntry__one_bid_one_ask_ok(){

        Long sequence = randSeq();
        List<JSONObject> bids = Collections.singletonList(order(153663.00, 0.00008714, "1ugNZMSlhr6MgHhq"));
        List<JSONObject> asks = Collections.singletonList(order(153753.00, 0.08061399, "g8fRXBUHlsangvo5"));

        JSONObject json = TestOrderHelpers.book(sequence,timestamp, Option.of(bids), Option.of(asks));

        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertThat(build.isSuccess(), is(true));
        assertThat(build.get().getSequence(), is(sequence));
        assertThat(build.get().getAsks(), arrayWithSize(1));
        assertThat(build.get().getBids(), arrayWithSize(1));
        assertThat(build.get().getBids()[0].getAmount(), is(closeTo(TransformHelpers.bd(0.00008714), SATOSHI_ERROR)));
        assertThat(build.get().getBids()[0].getPrice(), is(closeTo(TransformHelpers.bd(153663), MXN_ERROR)));
        assertThat(build.get().getBids()[0].getId(), is("1ugNZMSlhr6MgHhq"));
        assertThat(build.get().getBids()[0].getTimestamp(),is(DateTimeHelpers.parseDate(timestamp)));



    }

}
