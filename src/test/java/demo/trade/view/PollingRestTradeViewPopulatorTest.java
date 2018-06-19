package demo.trade.view;

import demo.trade.domain.RecentTradesLog;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class PollingRestTradeViewPopulatorTest {

    @Test
    public  void test_constructor(){

        final RecentTradesLog tradesLog = Mockito.mock(RecentTradesLog.class);

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingRestTradeViewPopulator(
                null,
                tradesLog));

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingRestTradeViewPopulator(10,
                null));

    }

}