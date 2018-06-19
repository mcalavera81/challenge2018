package demo.order.domain;

import demo.order.domain.Order.Ask;
import demo.order.domain.Order.Bid;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static demo.TestUtils.now;
import static demo.order.parser.TestOrderUtils.randOrderData;

public class OrderTest {

    @Test
    public void test_bid_constructor(){

        Assertions.assertThrows(NullPointerException.class,
            () -> new Bid(null, now()));

        Assertions.assertThrows(NullPointerException.class,
            () -> new Bid(randOrderData(), null));

    }

    @Test
    public void test_ask_constructor(){

        Assertions.assertThrows(NullPointerException.class,
            () -> new Ask(null, now()));

        Assertions.assertThrows(NullPointerException.class,
            () -> new Ask(randOrderData(), null));
    }

}