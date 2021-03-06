package demo.order.source.poller.client;

import demo.order.source.poller.dto.OrderBookSnapshot;
import demo.app.Constants.BitsoBook;
import demo.support.rest.RestClient;
import demo.support.rest.SimpleRateLimiter;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;

@Slf4j
public class RestOrderBookSnapshotSource extends RestClient<OrderBookSnapshot> implements
    OrderBookSource {

    public RestOrderBookSnapshotSource(
        BitsoBook book,
        String baseUri,
        AsyncHttpClient httpCient){

        super(book, baseUri, httpCient);
    }

    public RestOrderBookSnapshotSource(
        BitsoBook book,
        String baseUri,
        AsyncHttpClient httpClient,
        int permits,
        int periodSeconds){

        super(book,baseUri,httpClient, SimpleRateLimiter.build(permits,
            periodSeconds));
    }



    @Override
    public Try<OrderBookSnapshot> getOrderBook() {
        log.info("Calling OrderBook endpoint. Book: {}", book.id());
        return get(OrderBookSnapshot::build);
    }

    @Override
    protected String getURL(int... limitParam) {
        return String.format(
            "%s/v3/order_book?book=%s&aggregate=false",
            this.baseUri,
            book.id());
    }

}
