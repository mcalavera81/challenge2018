package demo.order.business.state;

import demo.order.business.state.Order.Ask;
import demo.order.business.state.Order.Bid;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static demo.order.TestOrderHelpers.randOrderData;
import static demo.support.helpers.DateTimeHelpers.now;

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