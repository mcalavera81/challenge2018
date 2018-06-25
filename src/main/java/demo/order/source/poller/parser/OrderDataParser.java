package demo.order.source.poller.parser;

import demo.support.helpers.WithId;
import demo.order.source.poller.dto.OrderData;
import demo.support.helpers.TransformHelpers;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.json.JSONObject;

import java.util.List;

import static demo.order.source.poller.parser.OrderDataParser.OrderField.*;
import static demo.support.parser.JsonParser.*;

public class OrderDataParser {

    enum OrderField implements WithId {
        ID("oid"),
        PRICE("price"),
        AMOUNT("amount");

        @Override
        public String id() {
            return id;
        }

        private final String id;
        OrderField(String id) { this.id = id; }
    }

    public static Try<OrderData> parseOrderData(JSONObject json){

        Validation<OrderDataParserException, OrderData> orderEntries =
                TransformHelpers.getValidation(getBigDecimal(json, PRICE)).
                        combine(TransformHelpers.getValidation(getBigDecimal(json, AMOUNT))).
                        combine(TransformHelpers.getValidation(getString(json,ID)))
                        .ap((price, amount, id) ->
                                OrderData.builder().id(id).price(price).amount(amount).build())
                        .mapError(error-> OrderDataParserException.build(error.asJava()));

        return TransformHelpers.toTry(orderEntries);


    }

    public static class OrderDataParserException extends Exception{
        OrderDataParserException(String message){
            super(message);
        }

        static OrderDataParserException build(List<String> messages){
            return new OrderDataParserException(String.join(",", messages));
        }
    }
}
