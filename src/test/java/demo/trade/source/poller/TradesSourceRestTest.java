package demo.trade.source.poller;

import demo.shared.config.AppConfiguration;
import demo.shared.parser.UtilParser.BitsoBook;
import demo.trade.business.state.Trade;
import demo.trade.source.dto.TradesBatch;
import io.vavr.control.Try;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static junit.framework.TestCase.assertTrue;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class TradesSourceRestTest {

    private TradesSource source;

    @Before
    public void setUp() {
        val conf = AppConfiguration.tryBuild();
        val bc = conf.getBackendConfig();
        source = new TradesSourceRest(
            BitsoBook.BTC_MXN,
            bc.getBitsoRestUri(),
            asyncHttpClient(),
            1,
            5);
    }

    @Test
    public void test_get_recent_trades_without_limit() {
        Try<TradesBatch> trades = source.getRecentTradesSortDesc();
        assertTrue(trades.isSuccess());
        assertThat(trades.get().getTrades(), hasSize(25));

    }

    @Test
    public void test_get_recent_trades_with_limit() {
        int maxTrades=4;
        TradesBatch batch = source.getRecentTradesSortDesc(maxTrades).get();
        List<Trade> trades = batch.getTrades();
        assertThat(batch.getTrades(), hasSize(maxTrades));

        assertThat(
            trades.get(0).getId(),
            is(greaterThan(trades.get(maxTrades-1).getId()))
        );

    }

}