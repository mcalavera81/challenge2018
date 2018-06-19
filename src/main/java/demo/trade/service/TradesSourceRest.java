package demo.trade.service;

import demo.shared.parser.UtilParser.BitsoBook;
import demo.shared.rest.RestClient;
import demo.shared.rest.SimpleRateLimiter;
import demo.trade.domain.TradesBatch;
import demo.trade.parser.TradeBatchParser;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;

@Slf4j
public class TradesSourceRest extends RestClient<TradesBatch> implements TradesSource {


    public TradesSourceRest(
        BitsoBook book,
        String baseUri,
        AsyncHttpClient httpClient,
        int permits,
        int periodSeconds){

        super(book,baseUri,httpClient, SimpleRateLimiter.build(permits,
            periodSeconds));
    }


    @Override
    public Try<TradesBatch> getRecentTradesSortDesc(int... limit) {
        log.info("Calling Trades endpoint. Book: {}", book.id());
        return get(TradeBatchParser::parseTrades,limit);
    }


    @Override
    protected String getURL(int... limitParam) {

        String request="%s/v3/trades?book=%s&sort=desc";

        if (limitParam != null && limitParam.length == 1) {
            request += "&limit="+limitParam[0];
        }


        return String.format(request, baseUri, book.id());
    }


}
