package demo.trade.source.parser;

import demo.shared.parser.UtilParser;
import demo.trade.business.state.Trade;
import demo.trade.source.dto.TradesBatch;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class TradeBatchParser {


    public static Try<TradesBatch> parseTrades(JSONObject o) {
        return UtilParser.parseRestJson(o, (JSONArray payload) ->
            geTradeEntries(payload).map(TradesBatch::new));
    }

    private static Try<List<Trade>> geTradeEntries(JSONArray array){

        List<Trade> entries;
        int count = array.length();
        entries = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            Try<Trade> build = Trade.build(array.getJSONObject(i));
            if(build.isFailure()){
                return Try.failure(build.getCause());
            }else{
                entries.add(build.get());
            }
        }
        return Try.success(entries);
    }


}
