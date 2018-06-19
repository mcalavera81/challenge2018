package demo.order.parser;

import demo.Constants;
import demo.TestUtils;
import demo.order.service.websocket.DiffOrdersBatch;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;
import org.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static demo.order.parser.DiffOrdersBatchParser.DiffOrdersBatchField.CHANNEL;
import static demo.order.parser.DiffOrdersBatchParser.isDiffOrderBatch;
import static demo.order.parser.DiffOrdersBatchParser.parseBatch;
import static demo.order.parser.TestParserOrderUtils.jsonDiffOrder;
import static demo.order.parser.TestParserOrderUtils.randOrderId;
import static demo.order.service.websocket.DiffOrder.OrderType.BUY;
import static demo.order.service.websocket.DiffOrder.OrderType.SELL;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;

public class DiffOrdersBatchParserTest {

    @Test
    public void empty_sequence(){
        JSONObject json = TestParserOrderUtils.orderBatch(Option.of(Collections.EMPTY_LIST), Option.none());
        Try<DiffOrdersBatch> updates = parseBatch(json);
        assertTrue(updates.isFailure());
        assertThat(updates.getCause().toString(), containsString("JSONObject[\"sequence\"] not found"));
    }

    @Test
    public void empty_payload(){
        JSONObject json = TestParserOrderUtils.orderBatch(Option.none(),
            Option.of(TestUtils.randSeq()));
        Try<DiffOrdersBatch> updates = parseBatch(json);
        assertTrue(updates.isFailure());
        assertThat(updates.getCause().toString(), containsString("JSONObject[\"payload\"] not found"));

    }

    @Test
    public void wrong_type(){
        JSONObject json = TestParserOrderUtils.orderBatch(Option.of(Collections.EMPTY_LIST),
            Option.of(TestUtils.randSeq()));
        json.put(CHANNEL.id(),"Wrong");

        Try<DiffOrdersBatch> updates = parseBatch(json);
        assertTrue(updates.isFailure());
        assertThat(updates.getCause().toString(), containsString("Wrong typ"));

    }

    @Test
    public void diff_order_batch_open(){
        String filename = "order/diff_order_batch_open.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        Try<DiffOrdersBatch> updateTry = parseBatch(json.get());
        assertTrue(updateTry.isSuccess());
        DiffOrdersBatch update = updateTry.get();
        assertThat(update.getOrders(), arrayWithSize(1));

    }

    @Test
    public void diff_order_batch_cancelled(){
        String filename = "order/diff_order_batch_cancelled.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        Try<DiffOrdersBatch> updateTry = parseBatch(json.get());
        assertTrue(updateTry.isSuccess());
        DiffOrdersBatch update = updateTry.get();
        assertThat(update.getOrders(), arrayWithSize(1));
        assertEquals(BigDecimal.ZERO, update.getOrders()[0].getAmount());
        assertThat(BigDecimal.ZERO, is(closeTo(update.getOrders()[0]
            .getValue(), Constants.MXN_ERROR)));

    }

    @Test
    public void two_diff_orders(){

        // ---------------- GIVEN ---------------
        Long seqId = TestUtils.randSeq();
        val o1 = jsonDiffOrder(randOrderId(), BUY);
        val o2 = jsonDiffOrder(randOrderId(), SELL);

        List<JSONObject> orders = Arrays.asList(o1._1,o2._1);

        JSONObject json = TestParserOrderUtils.orderBatch(Option.of(orders),
            Option.of(seqId));
        // ---------------- WHEN ---------------

        Try<DiffOrdersBatch> updates = parseBatch(json);

        // ---------------- THEN ---------------

        assertTrue(updates.isSuccess());
        assertThat(updates.get().getSequence(), is(seqId));
        assertThat(updates.get().getOrders(), arrayWithSize(2));
        assertEquals(o1._2, updates.get().getOrders()[0]);
        assertEquals(o2._2, updates.get().getOrders()[1]);
    }

    @Test
    public void test_diffOrder_message_detector(){
        String filename = "order/diff_order_batch_open.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        assertTrue(isDiffOrderBatch(json.get()));
    }


    @Test
    public void test_subscribe_response(){
        String filename = "order/subscribe_response_ok.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        assertFalse(isDiffOrderBatch(json.get()));


    }

    @Test
    public void test_keep_alive_message(){
        String filename = "order/keep_alive.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        assertFalse(isDiffOrderBatch(json.get()));


    }

    @Test
    public void test_batch_with_malformed_order(){
        String filename = "order/diff_order_batch_failed_order.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        Try<DiffOrdersBatch> batch = parseBatch(json.get());
        assertTrue(batch.isFailure());
        assertThat(batch.getCause(), is(instanceOf
            (DiffOrdersBatchParser.DiffOrderParserException.class)));

        assertThat(batch.getCause().toString(), containsString(" JSONObject[\"o\"] not found"));

    }

}


