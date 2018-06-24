package demo.order.source.poller;

import demo.order.source.poller.dto.OrderBookSnapshot;
import demo.order.source.poller.dto.OrderData;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static demo.shared.parser.UtilParser.now;

public class OrderBookSnapshotTest {

    @Test
    public void test_constructor(){

        Assertions.assertThrows(NullPointerException.class,
            ()->new OrderBookSnapshot(
                1,
                null,
                new OrderData[0],
                new OrderData[0]
            ));

        Assertions.assertThrows(NullPointerException.class,
            ()->new OrderBookSnapshot(
                1,
                now(),
                null,
                new OrderData[0]
            ));

        Assertions.assertThrows(NullPointerException.class,
            ()->new OrderBookSnapshot(
                1,
                now(),
                new OrderData[0],
            null
            ));

    }

}