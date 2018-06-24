package demo.trade.business.algorithm;

import demo.trade.business.state.Trade;
import demo.trade.business.state.Trade.TradeType;
import demo.trade.parser.TestTradeUtils;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static demo.trade.business.state.Trade.TradeType.BUY;
import static demo.trade.business.state.Trade.TradeType.SELL;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ContrarianTradingStrategyTest {


    private TradingAlgorithm simulator;

    @Before
    public void init() {
        int up_ticks = 3;
        int down_ticks = 2;
        this.simulator = new ContrarianTradingStrategy(
            up_ticks,
            down_ticks);
    }


    @Test
    public void test_no_triggers(){

        double[] pricesDesc = {40, 20, 10, 20, 10, 0};
        val trades = TestTradeUtils.randTradesWithPrices(pricesDesc);
        simulator.run(trades);

        assertThat(trades, hasSize(pricesDesc.length));
    }

    @Test
    public void test_one_sell_one_buy(){

        double[] pricesDesc = {70, 11, 12, 16, 18, 11, 10.5, 10, 10, 9.5, 10, 0};
        val trades = TestTradeUtils.randTradesWithPrices(pricesDesc);
        simulator.run(trades);

        assertThat(trades, hasSize(pricesDesc.length+2));

        final val sell = trades.get(6);
        assertSimulatedTrade(sell, 11, SELL,-1);


        final val buy = trades.get(2);
        assertSimulatedTrade(buy, 12, BUY,-2);

    }


    @Test
    public void test_two_sells_with_reprocessing(){

        double[] pricesDesc = {101, 90, 73, 62, 50, 20, 10, 0};
        val trades = TestTradeUtils.randTradesWithPrices(pricesDesc);
        simulator.run(trades);

        assertThat(trades, hasSize(pricesDesc.length + 2));

        final val sell1 = trades.get(5);
        assertSimulatedTrade(sell1, 50, SELL,-1);


        final val sell2 = trades.get(1);
        assertSimulatedTrade(sell2, 90, SELL,-2);

        simulator.run(trades);

        assertThat(trades, hasSize(pricesDesc.length + 2));

    }


    @Test
    public void test_selling_after_3_upticks(){

        final double[] pricesDesc = {30, 20, 10, 0};
        val trades = TestTradeUtils.randTradesWithPrices(pricesDesc);
        simulator.run(trades);


        assertThat(trades, hasSize(pricesDesc.length+1));

        final Trade fake = trades.get(0);
        assertSimulatedTrade(fake, 30, SELL,-1);


    }

    @Test
    public void test_selling_after_3_upticks_reprocessing(){

        final double[] pricesDesc = {30, 20, 10, 0};
        val trades = TestTradeUtils.randTradesWithPrices(pricesDesc);
        simulator.run(trades);
        simulator.run(trades);

        assertThat(trades, hasSize(pricesDesc.length+1));
        final Trade fake = trades.get(0);
        assertSimulatedTrade(fake, 30, SELL,-1);

    }

    @Test
    public void test_2_downticks(){

        final double[] pricesDesc = {10, 20, 30, 0};
        val trades = TestTradeUtils.randTradesWithPrices(pricesDesc);
        simulator.run(trades);

        assertThat(trades, hasSize(pricesDesc.length+1));
        final Trade fake = trades.get(0);
        assertSimulatedTrade(fake, 10, BUY,-1);

    }

    @Test
    public void test_2_downticks_reprocessing(){

        final double[] pricesDesc = {10, 20, 30, 0};
        val trades = TestTradeUtils.randTradesWithPrices(pricesDesc);
        simulator.run(trades);
        simulator.run(trades);

        assertThat(trades, hasSize(pricesDesc.length+1));
        final Trade fake = trades.get(0);
        assertSimulatedTrade(fake, 10, BUY,-1);


    }

    private void assertSimulatedTrade(Trade t,
                                      double price,
                                      TradeType type,
                                      long id) {

        assertEquals(price,t.getPrice().doubleValue(),0.01);
        assertEquals(Trade.TradeSource.SIMULATED, t.getSource());
        assertEquals(type, t.getType());
        assertEquals(id, t.getId().longValue());
        assertEquals(BigDecimal.ONE, t.getAmount());
    }
}