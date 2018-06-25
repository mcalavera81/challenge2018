package demo.trade.business.state;

import demo.trade.business.algorithm.NoTradingStrategy;
import demo.trade.business.algorithm.TradingStrategy;
import demo.trade.TestTradeHelpers;
import demo.trade.source.dto.TradesBatch;
import demo.trade.source.poller.TradesSource;
import io.vavr.control.Try;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LatestTradesContainerTest {


    private static int CACHE_SIZE = 8;

    private final Integer DUMMY_INT=0;
    private TradesSource tradesSource;
    private TradingStrategy algorithm;


    @Before
    public void init(){

        tradesSource = mock(TradesSource.class);
        algorithm = mock(TradingStrategy.class);
    }


    @Test
    public void test_constructor_trades_source_null() {

        Assertions.assertThrows(
            NullPointerException.class,
            () -> new LatestTradesContainer(
                null,
                DUMMY_INT,
                DUMMY_INT,
                algorithm));
    }

    @Test
    public void test_constructor_cache_size_null() {

        Assertions.assertThrows(
            NullPointerException.class,
            () -> new LatestTradesContainer(
                tradesSource,
                null,
                DUMMY_INT,
                algorithm));
    }

    @Test
    public void test_constructor_rest_default_limit_null() {

        Assertions.assertThrows(
            NullPointerException.class,
            () -> new LatestTradesContainer(
                tradesSource,
                DUMMY_INT,
                null,
                algorithm));
    }

    public void test_constructor_rest_trading_algorithm_null() {
        Assertions.assertThrows(
            NullPointerException.class,
            ()->new LatestTradesContainer(
                tradesSource,
                DUMMY_INT,
                DUMMY_INT,
                null));


    }


    @Test
    public void test_first_batch_load() {

        // ------------------ GIVEN ------------------------
        final int restLimit = 5;
        final int maxTrades = 3;
        final int batch_maxId = 5;

        LatestTradesContainer tradesContainer = prepareTradesContainer(
            restLimit,
            Collections.singletonList(batch_maxId));

        assertEquals(0, tradesContainer.size());
        // ------------------ WHEN ------------------------
        val recentTrades = tradesContainer.getRecentTrades(maxTrades);


        // ------------------ THEN ------------------------
        assertEquals(maxTrades, recentTrades.size());
        assertEquals(restLimit, tradesContainer.size());

        assertEquals(batch_maxId, recentTrades.get(0).getId().intValue());
        assertEquals(batch_maxId - 2, recentTrades.get(2).getId().intValue());

    }

    @Test
    public void test_load_same_date_multiple_times() {
        // ------------------ GIVEN ------------------------
        final int restLimit = 5;
        final int query = 3;
        final int batch_maxId = 5;


        LatestTradesContainer simulator = prepareTradesContainer(
            restLimit,
            Collections.singletonList(batch_maxId));

        assertEquals(0, simulator.size());
        // ------------------ WHEN ------------------------
        simulator.getRecentTrades(query);
        val recentTrades = simulator.getRecentTrades(query);
        // ------------------ THEN ------------------------
        assertEquals(query, recentTrades.size());
        assertEquals(restLimit, simulator.size());

        assertEquals(batch_maxId, recentTrades.get(0).getId().intValue());
        assertEquals(batch_maxId - 2, recentTrades.get(2).getId().intValue());


    }

    @Test
    public void test_load_second_batch_no_overlap() {

        // ------------------ GIVEN ------------------------
        final int restLimit = 5;
        final int query = 3;
        final int maxId_batch1 = 5;
        final int maxId_batch2 = 11;


        LatestTradesContainer simulator = prepareTradesContainer(
            restLimit,
            Arrays.asList(maxId_batch1, maxId_batch2));


        // ------------------ WHEN ------------------------
        simulator.getRecentTrades(query);
        val recentTrades = simulator.getRecentTrades(query);


        // ------------------ THEN ------------------------
        assertEquals(query, recentTrades.size());
        assertEquals(CACHE_SIZE, simulator.size());

        assertEquals(maxId_batch2, recentTrades.get(0).getId().intValue());
        assertEquals(maxId_batch2 - 2, recentTrades.get(2).getId().intValue());

        assertEquals(3,
            simulator.getRecentTrades(CACHE_SIZE).get(CACHE_SIZE-1).getId().intValue());

    }

    @Test
    public void test_load_second_batch_with_overlap() {

        // ------------------ GIVEN ------------------------
        final int restLimit = 5, maxTrades = 3, maxId_batch1 = 5, maxId_batch2 = 8;


        LatestTradesContainer simulator = prepareTradesContainer(
            restLimit,
            Arrays.asList(maxId_batch1, maxId_batch2));

        // ------------------ WHEN ------------------------
        simulator.getRecentTrades(maxTrades);
        val recentTrades = simulator.getRecentTrades(maxTrades);


        // ------------------ THEN ------------------------
        assertEquals(maxTrades, recentTrades.size());
        assertEquals(CACHE_SIZE, simulator.size());

        assertEquals(maxId_batch2, recentTrades.get(0).getId().intValue());
        assertEquals(maxId_batch2 - 2, recentTrades.get(2).getId().intValue());

        assertEquals(1,
            simulator.getRecentTrades(CACHE_SIZE).get(CACHE_SIZE-1).getId().intValue());


    }

    private LatestTradesContainer prepareTradesContainer(int restLimit,List<Integer> maxIds) {
        final TradesSource source = Mockito.mock(TradesSource.class);
        when(source.getRecentTradesSortDesc(Mockito.any())).then(
            new CustomAnswer(restLimit, maxIds)
        );
        return new LatestTradesContainer(
            source,
            CACHE_SIZE,
            restLimit,
            new NoTradingStrategy());
    }

    static class CustomAnswer implements Answer<Try<TradesBatch>>{


        private int restLimit;
        private List<Integer> maxIds;

        CustomAnswer(int restLimit, List<Integer> maxIds){
            this.restLimit = restLimit;
            this.maxIds = maxIds;
        }

        int count = -1;

        @Override
        public Try<TradesBatch> answer(InvocationOnMock invocation) throws Throwable {

            count = (count+1)%maxIds.size();
            return TestTradeHelpers.randTradesBatch(restLimit, maxIds.get(count));
        }
    }

}