package demo.trade.view;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class RecentTradesControllerTest {


    @Test
    public void test_constructor(){
        Assertions.assertThrows(NullPointerException.class,
            () -> new RecentTradesController(null));
    }
}