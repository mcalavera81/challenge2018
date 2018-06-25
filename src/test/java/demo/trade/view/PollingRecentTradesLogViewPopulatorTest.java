package demo.trade.view;

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import demo.trade.business.state.RecentTradesLog;
import javafx.beans.property.ListProperty;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static demo.trade.TestTradeHelpers.randTrades;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;

//195
@RunWith(JfxRunner.class)
public class PollingRecentTradesLogViewPopulatorTest {


    private PollingRecentTradesLogViewPopulator viewPopulator;
    private ListProperty<TradeTableRow> tradesProp;

    @Test
    public  void test_constructor(){

        final RecentTradesLog tradesLog = Mockito.mock(RecentTradesLog.class);

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingRecentTradesLogViewPopulator(
                null,
                10,
                tradesLog));

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingRecentTradesLogViewPopulator(
                3000,
                null,
                tradesLog));

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingRecentTradesLogViewPopulator(
                3000,
                10,
                null));

    }

    @Test
    public void test_update_trade_prop_from_latest_trades() throws
        Exception {

        int maxTrades = 10;
        int pollingIntervalMs = 1000;
        final RecentTradesLog recentTradesLog = Mockito.mock(RecentTradesLog.class);

        Mockito.when(recentTradesLog.getRecentTrades(anyInt()))
            .thenReturn(randTrades(4));

        viewPopulator = new
            PollingRecentTradesLogViewPopulator(
            pollingIntervalMs,
            maxTrades,
            recentTradesLog);

        this.tradesProp = viewPopulator.getTradeProp();

        viewPopulator.start();
        Thread.sleep(pollingIntervalMs*2);

        assertTrades();

    }

    private void assertTrades() {
        MatcherAssert.assertThat(tradesProp, hasSize(4));

    }
}