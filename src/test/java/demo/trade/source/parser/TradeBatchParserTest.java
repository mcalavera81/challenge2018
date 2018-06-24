package demo.trade.source.parser;

import demo.TestUtils;
import demo.order.source.poller.parser.OrderBookSnaphsotParser.ResponseServerException;
import demo.shared.parser.UtilParser.ParserException;
import demo.trade.source.dto.TradesBatch;
import demo.trade.source.parser.TradeParser.TradeParserException;
import io.vavr.control.Try;
import lombok.val;
import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TradeBatchParserTest {

    @Test
    public void trade_batch_missing_success(){
        String filename="trade/trade_batch_missing_success.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        Try<TradesBatch> tradeBatch = TradesBatch.build(json.get());
        assertTrue(tradeBatch.isFailure());
        assertThat(tradeBatch.getCause(), is(instanceOf(ParserException.class)));


    }

    @Test
    public void trade_batch_missing_payload(){
        String filename="trade/trade_batch_missing_payload.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        Try<TradesBatch> tradeBatch = TradesBatch.build(json.get());
        assertTrue(tradeBatch.isFailure());
        assertThat(tradeBatch.getCause(), is(instanceOf(ParserException.class)));


    }

    @Test
    public void test_order_book_error() {
        String filename = "trade/trade_batch_error.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        val tradeBatch = TradesBatch.build(json.get());
        assertTrue(tradeBatch.isFailure());

        assertThat(tradeBatch.getCause(), is(instanceOf(ResponseServerException.class)));

    }

    @Test
    public void trade_batch_ok(){
        String filename="trade/trade_batch_ok.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        val tradeBatch = TradesBatch.build(json.get());
        assertTrue(tradeBatch.isSuccess());
        assertThat(tradeBatch.get().getTrades(), hasSize(25));


    }

    @Test
    public void trade_batch_failed_trade(){
        String filename="trade/trade_batch_failed_trade.json";
        Try<JSONObject> json = TestUtils.loadJson(filename);

        val tradeBatch = TradesBatch.build(json.get());
        assertTrue(tradeBatch.isFailure());
        assertThat(tradeBatch.getCause(), is(instanceOf
            (TradeParserException.class)));
        assertThat(tradeBatch.getCause().toString(),
            containsString(" Not valid trade type"));


    }
}
