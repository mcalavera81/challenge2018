package demo.order.view;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class OrderBookControllerTest {

    @Test
    public void test_constructor(){
        Assertions.assertThrows(NullPointerException.class,
            () -> new OrderBookController(null));
    }

}