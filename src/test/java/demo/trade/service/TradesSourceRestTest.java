package demo.trade.service;

import demo.shared.parser.UtilParser.BitsoBook;
import demo.shared.config.AppConfiguration;
import demo.trade.domain.Trade;
import demo.trade.domain.TradesBatch;
import io.vavr.control.Try;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

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
        assertThat(trades.get().getTrades(), Matchers.arrayWithSize(25));

    }

    @Test
    public void test_get_recent_trades_with_limit() {
        int maxTrades=4;
        TradesBatch batch = source.getRecentTradesSortDesc(maxTrades).get();
        Trade[] trades = batch.getTrades();
        assertThat(batch.getTrades(), Matchers.arrayWithSize(maxTrades));

        assertThat(
            trades[0].getId(),
            is(greaterThan(trades[maxTrades-1].getId()))
        );

    }

}