package demo.order.source.stream;

import demo.order.source.stream.client.OrderBuffer;
import lombok.val;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static demo.order.TestOrderHelpers.orderBatch;
import static demo.order.source.stream.dto.DiffOrder.OrderType.BUY;
import static demo.order.source.stream.dto.DiffOrder.OrderType.SELL;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBufferTest {


    private OrderBuffer buffer;
    private final String ORDER_ID= "dummy";
    @Before
    public void init(){
        int bufferSize = 3;
        buffer = new OrderBuffer(bufferSize);
    }
    @Test
    public void test_consume_message(){
        int sequence=0;
        buffer.addMessage(orderBatch(BUY, ++sequence, ORDER_ID).toString());
        buffer.addMessage(orderBatch(SELL,++sequence, ORDER_ID).toString());
        buffer.addMessage(orderBatch(SELL, ++sequence, ORDER_ID).toString());


        assertEquals(buffer.consume().get().get().getSequence(),1);

    }

    @Test
    public void test_consume_message_after_full_buffer(){
        int sequence=0;
        buffer.addMessage(orderBatch(BUY, ++sequence, ORDER_ID).toString());
        buffer.addMessage(orderBatch(SELL,++sequence, ORDER_ID).toString());
        buffer.addMessage(orderBatch(SELL, ++sequence, ORDER_ID).toString());
        buffer.addMessage(orderBatch(SELL, ++sequence, ORDER_ID).toString());

        assertEquals(buffer.consume().get().get().getSequence(),sequence);

    }

    @Test
    public void test_consume_malformed_message(){
        int sequence=0;
        buffer.addMessage("");
        val message = buffer.consume().get();
        assertTrue(message.isFailure());
        assertThat(message.getCause(), is(instanceOf(JSONException.class)));
    }

}