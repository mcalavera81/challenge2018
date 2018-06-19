package demo.trade.domain;

import lombok.val;
import org.junit.Test;

public class ContrarianTradingStrategySimulatorTest {


    @Test
    public void test_trade_not_triggered(){
        int UP_TICKS= 3;
        int DOWN_TICKS= 2;
        val simulator = new ContrarianTradingStrategySimulator(UP_TICKS,
            DOWN_TICKS);



        //simulator.run();
    }

}