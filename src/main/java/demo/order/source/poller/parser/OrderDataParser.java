package demo.order.source.poller.parser;

import demo.order.helpers.WithId;
import demo.order.source.poller.dto.OrderData;
import demo.shared.parser.UtilParser;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.json.JSONObject;

import java.util.List;

import static demo.order.source.poller.parser.OrderDataParser.OrderField.*;
import static demo.shared.parser.UtilParser.*;

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
                getValidation(getBigDecimal(json, PRICE)).
                        combine(getValidation(getBigDecimal(json, AMOUNT))).
                        combine(getValidation(getString(json,ID)))
                        .ap((price, amount, id) ->
                                OrderData.builder().id(id).price(price).amount(amount).build())
                        .mapError(error-> OrderDataParserException.build(error.asJava()));

        return UtilParser.toTry(orderEntries);


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
