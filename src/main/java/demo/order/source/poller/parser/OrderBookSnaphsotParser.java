package demo.order.source.poller.parser;

import demo.support.helpers.WithId;
import demo.order.source.poller.dto.OrderBookSnapshot;
import demo.order.source.poller.dto.OrderData;
import demo.support.helpers.TransformHelpers;
import demo.support.parser.JsonParser;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.List;

import static demo.order.source.poller.parser.OrderBookSnaphsotParser.ResponseServerException.ResponseServerErrorField.CODE;
import static demo.order.source.poller.parser.OrderBookSnaphsotParser.ResponseServerException.ResponseServerErrorField.MESSAGE;
import static demo.support.parser.JsonParser.*;

@Slf4j
public class OrderBookSnaphsotParser {


    public enum OrderBookField implements WithId {
        BIDS("bids"),
        ASKS("asks"),
        UPDATED_AT("updated_at"),
        BOOK_SEQUENCE("sequence");

        @Override
        public String id() {
            return id;
        }

        private final String id;
        OrderBookField(String id) { this.id = id; }
    }


    public static Try<OrderBookSnapshot> parseOrderBook(JSONObject o){

        return JsonParser.parseRestJson(o, (JSONObject payload) -> {

            Validation<OrderBookParserException, OrderBookSnapshot> orderBooks =
                    TransformHelpers.getValidation(getLong(payload, OrderBookField.BOOK_SEQUENCE))
                            .combine(TransformHelpers.getValidation(getDateFromString(payload,OrderBookField.UPDATED_AT)))
                            .combine(geOrderEntries(payload, OrderBookField.BIDS))
                            .combine(geOrderEntries(payload, OrderBookField.ASKS))
                            .ap(OrderBookSnapshot::new)
                            .mapError(error -> OrderBookParserException.build(error.asJava()));

            return TransformHelpers.toTry(orderBooks);
        });
    }

    private static Validation<String,OrderData[]> geOrderEntries(JSONObject json, OrderBookField name){
        return TransformHelpers.getValidation(getJsonArray(json,name)).flatMap(array->
        {
            OrderData[] entries;
            int count = array.length();
            entries = new OrderData[count];
            for (int i = 0; i < count; i++) {
                Try<OrderData> build = OrderData.build(array.getJSONObject(i));
                if(build.isFailure()){
                    return Validation.invalid(build.getCause().toString());
                }else{
                    entries[i] = build.get();
                }
            }
            return Validation.valid(entries);

        });
    }


    public static class OrderBookParserException extends Exception{
        OrderBookParserException(String message){
            super(message);
        }

        public OrderBookParserException(Throwable cause) {
            super(cause);
        }

        static OrderBookParserException build(List<String> messages){
            return new OrderBookParserException(String.join(",", messages));
        }
    }

    public static class ResponseServerException extends Exception{

        public enum ResponseServerErrorField implements WithId {
            CODE("code"),
            MESSAGE("message");

            @Override
            public String id() {
                return id;
            }

            final String id;
            ResponseServerErrorField(String id) { this.id = id; }

        }

        private ResponseServerException(String message){
            super(message);
        }

        public static ResponseServerException build(JSONObject jsonError) {
            return new ResponseServerException(
                    String.format("Error response code: %d, message: %s",
                    getLong(jsonError, CODE).getOrElse(-1L),
                    getString(jsonError, MESSAGE).getOrElse("")));

        }

    }

}
