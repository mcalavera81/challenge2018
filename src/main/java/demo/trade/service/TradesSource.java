package demo.trade.service;

import demo.trade.domain.TradesBatch;
import io.vavr.control.Try;

public interface TradesSource {

    Try<TradesBatch> getRecentTradesSortDesc(int... limit);

}
