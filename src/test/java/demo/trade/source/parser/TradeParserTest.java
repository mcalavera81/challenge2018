package demo.trade.source.parser;

import demo.trade.business.state.Trade;
import demo.trade.parser.TestTradeUtils;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import org.json.JSONObject;
import org.junit.Test;

import static demo.TestUtils.loadJson;
import static demo.trade.source.parser.TradeParser.TradeField.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TradeParserTest {

    @Test
    public void parse_trade_empty(){
        JSONObject jsonObject = new JSONObject("{}");
        Try<Trade> trade = Trade.build(jsonObject);
        assertThat(trade.isSuccess(), is(false));
        assertThat(trade.getCause(), is(instanceOf(TradeParser.TradeParserException.class)));

    }

    @Test
    public void parse_trade_full(){
        String filename = "trade/trade_ok.json";
        Try<JSONObject> jsonObject = loadJson(filename);
        Try<Trade> trade = Trade.build(jsonObject.get());
        assertThat(trade.isSuccess(), is(true));


    }

    @Test
    public void parse_trade_missing_id(){

        Tuple2<JSONObject, Trade> tuple = TestTradeUtils.jsonTrade();
        tuple._1().remove(ID.id());
        assertMissingField(tuple._1, ID);

    }
    @Test
    public void parse_trade_missing_amount(){

        Tuple2<JSONObject, Trade> tuple = TestTradeUtils.jsonTrade();
        tuple._1().remove(AMOUNT.id());
        assertMissingField(tuple._1, AMOUNT);



    }

    @Test
    public void parse_trade_missing_price(){

        Tuple2<JSONObject, Trade> tuple = TestTradeUtils.jsonTrade();
        tuple._1().remove(PRICE.id());
        assertMissingField(tuple._1, PRICE);

    }


    @Test
    public void parse_trade_missing_side(){

        Tuple2<JSONObject, Trade> tuple = TestTradeUtils.jsonTrade();
        tuple._1().remove(SIDE.id());
        assertMissingField(tuple._1, SIDE);




    }


    private void assertMissingField(
        JSONObject json,
        TradeParser.TradeField field){

        Try<Trade> trade = Trade.build(json);
        assertThat(trade.isSuccess(), is(false));
        assertThat(trade.getCause().toString(), containsString
            (String.format("JSONObject[\"%s\"] not found", field.id())));
    }

}
