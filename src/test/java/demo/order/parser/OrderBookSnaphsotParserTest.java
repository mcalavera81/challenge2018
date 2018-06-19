package demo.order.parser;

import demo.TestUtils;
import demo.order.domain.OrderBookSnapshot;
import demo.order.parser.OrderBookSnaphsotParser.ResponseServerException;
import demo.order.parser.UtilParser.ParserException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static demo.Constants.MXN_ERROR;
import static demo.Constants.SATOSHI_ERROR;
import static demo.TestUtils.randSeq;
import static demo.order.parser.TestOrderUtils.order;
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
        Try<JSONObject> json = TestUtils.loadJson(filename);

        val obTry = OrderBookSnapshot.build(json.get());
        assertTrue(obTry.isFailure());

        assertThat(obTry.getCause(), is(instanceOf(ResponseServerException.class)));

    }

    @Test
    public void test_order_book_ok() {
        String filename = "order/order_book_ok.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

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

        JSONObject jsonObject = TestOrderUtils.book(randSeq(), timestamp, Option.of(Collections.EMPTY_LIST),Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> ob = OrderBookSnapshot.build(jsonObject);
        assertTrue(ob.isSuccess());
        assertThat(ob.get().getAsks(), emptyArray());
        assertThat(ob.get().getBids(), emptyArray());

    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_oid(){

        List<JSONObject> bids = Arrays.asList(order(153663.00,0.00008714,null));

        JSONObject json = TestOrderUtils.book(randSeq(),timestamp , Option.of(bids), Option.of(Collections.EMPTY_LIST));

        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"oid\"] not found"));


    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_amount(){

        List<JSONObject> bids = Arrays.asList(order(153663.00,null,"1ugNZMSlhr6MgHhq"));

        JSONObject json = TestOrderUtils.book(randSeq(),timestamp , Option.of(bids),Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"amount\"] not found"));

    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_price(){

        List<JSONObject> bids = Arrays.asList(order(null,0.00008714,"1ugNZMSlhr6MgHhq"));

        JSONObject json = TestOrderUtils.book(randSeq(), timestamp, Option.of(bids), Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"price\"] not found"));

    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_bids(){

        JSONObject json = TestOrderUtils.book(randSeq(),timestamp , Option.none(), Option.of(Collections.EMPTY_LIST));
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"bids\"] not found"));
    }

    @Test
    public void test_build_OrderEntry__one_bid_missing_asks(){

        JSONObject json = TestOrderUtils.book(randSeq(), timestamp, Option.of(Collections
                .EMPTY_LIST), Option.none());
        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertTrue(build.isFailure());
        assertThat(build.getCause().toString(), containsString("JSONObject[\"asks\"] not found"));
    }

    @Test
    public void test_build_OrderEntry__one_bid_one_ask_ok(){

        Long sequence = randSeq();
        List<JSONObject> bids = Arrays.asList(order(153663.00,0.00008714,"1ugNZMSlhr6MgHhq"));
        List<JSONObject> asks = Arrays.asList(order(153753.00, 0.08061399, "g8fRXBUHlsangvo5"));

        JSONObject json = TestOrderUtils.book(sequence,timestamp, Option.of(bids), Option.of(asks));

        Try<OrderBookSnapshot> build = OrderBookSnapshot.build(json);
        assertThat(build.isSuccess(), is(true));
        assertThat(build.get().getSequence(), is(sequence));
        assertThat(build.get().getAsks(), arrayWithSize(1));
        assertThat(build.get().getBids(), arrayWithSize(1));
        assertThat(build.get().getBids()[0].getAmount(), is(closeTo(BigDecimal.valueOf(0.00008714), SATOSHI_ERROR)));
        assertThat(build.get().getBids()[0].getPrice(), is(closeTo(BigDecimal.valueOf(153663), MXN_ERROR)));
        assertThat(build.get().getBids()[0].getId(), is("1ugNZMSlhr6MgHhq"));
        assertThat(build.get().getBids()[0].getTimestamp(),is(UtilParser.parseDate(timestamp)));



    }

}
