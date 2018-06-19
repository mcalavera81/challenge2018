package demo.trade.domain;


import demo.trade.parser.TradeBatchParser;
import io.vavr.control.Try;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class TradesBatch {
    private final Trade[] trades;

    public TradesBatch(Trade[] trades) {
        this.trades = trades;
    }

    public static Try<TradesBatch> build(JSONObject json){
        return TradeBatchParser.parseTrades(json);
    }
}
