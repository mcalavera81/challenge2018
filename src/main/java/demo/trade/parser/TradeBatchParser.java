package demo.trade.parser;

import demo.shared.parser.UtilParser;
import demo.trade.domain.Trade;
import demo.trade.domain.TradesBatch;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
public class TradeBatchParser {


    public static Try<TradesBatch> parseTrades(JSONObject o) {
        return UtilParser.parseRestJson(o, (JSONArray payload) ->
            geTradeEntries(payload).map(TradesBatch::new));
    }

    private static Try<Trade[]> geTradeEntries(JSONArray array){

        Trade[] entries;
        int count = array.length();
        entries = new Trade[count];

        for (int i = 0; i < count; i++) {
            Try<Trade> build = Trade.build(array.getJSONObject(i));
            if(build.isFailure()){
                return Try.failure(build.getCause());
            }else{
                entries[i] = build.get();
            }
        }
        return Try.success(entries);
    }


}
