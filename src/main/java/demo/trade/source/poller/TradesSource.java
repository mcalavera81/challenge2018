package demo.trade.source.poller;

import demo.trade.source.dto.TradesBatch;
import io.vavr.control.Try;

public interface TradesSource {

    Try<TradesBatch> getRecentTradesSortDesc(int... limit);

}
