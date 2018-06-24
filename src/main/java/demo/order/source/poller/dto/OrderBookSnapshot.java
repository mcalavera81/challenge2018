package demo.order.source.poller.dto;

import demo.order.business.state.Order.Ask;
import demo.order.business.state.Order.Bid;
import demo.order.source.poller.parser.OrderBookSnaphsotParser;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

@Getter
public class OrderBookSnapshot {


    private final long sequence;
    private final ZonedDateTime timestamp;
    private final Bid[] bids;
    private final Ask[] asks;

    public OrderBookSnapshot(
        long sequence,
        @NonNull ZonedDateTime timestamp,
        @NonNull OrderData[] bids,
        @NonNull OrderData[] asks) {

        this.sequence = sequence;
        this.timestamp = timestamp;

        this.bids = Stream.of(bids)
            .map(orderData -> new Bid(orderData,timestamp))
            .toArray(Bid[]::new);

        this.asks = Stream.of(asks)
            .map(orderData -> new Ask(orderData,timestamp))
            .toArray(Ask[]::new);

    }

    public static Try<OrderBookSnapshot> build(JSONObject json){
        return OrderBookSnaphsotParser.parseOrderBook(json);
    }



}
