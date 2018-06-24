package demo.trade.source.dto;


import demo.trade.business.state.Trade;
import demo.trade.source.parser.TradeBatchParser;
import io.vavr.control.Try;
import lombok.Getter;
import org.json.JSONObject;

import java.util.List;

@Getter
public class TradesBatch {
    private final List<Trade> trades;

    public TradesBatch(List<Trade> trades) {
        this.trades = trades;
    }

    public static Try<TradesBatch> build(JSONObject json){
        return TradeBatchParser.parseTrades(json);
    }
}
