package demo.order.parser;

import demo.order.domain.OrderData;
import demo.order.service.websocket.DiffOrder;
import demo.order.service.websocket.DiffOrder.OrderStatus;
import demo.order.service.websocket.DiffOrder.OrderType;
import demo.shared.parser.UtilParser;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

import static demo.order.parser.DiffOrderParser.DiffOrderField.*;
import static demo.shared.parser.UtilParser.*;

public class DiffOrderParser {

    enum DiffOrderField implements WithId{

        TIMESTAMP("d"),     //Unix timestamp	Milliseconds
        RATE("r"),          //Rate	Minor
        TYPE("t"),          //0 indicates buy 1 indicates sell
        AMOUNT("a"),        //Amount	Major
        VALUE("v"),         //Value	Minor
        ORDER_ID("o"),      //Order ID
        STATUS("s");

        private final String id;
        DiffOrderField(String id) { this.id = id; }
        public String id() { return this.id;}
    }

    public static Try<DiffOrder> parseDiffOrder(JSONObject o) {
        Validation<DiffOrderException, DiffOrder> diffOrder =
            getValidation(getDateFromMillis(o, TIMESTAMP))
                .combine(getValidation(getBigDecimal(o, RATE)))
                .combine(getValidation((getInt(o, TYPE).flatMap(OrderType::fromInt))))
                .combine(getValidation(getString(o, ORDER_ID)))
                .ap((date, rate, type, orderId) ->{


                    OrderData orderData = OrderData.builder()
                        .id(orderId)
                        .amount(extractAmount(o))
                        .price(rate)
                        .build();

                    return new DiffOrder(
                        type,
                        orderData,
                        date,
                        extractStatus(o));

                })
                .mapError(error -> DiffOrderException.build(error.asJava()));

        return UtilParser.toTry(diffOrder);

    }

    private static OrderStatus extractStatus(JSONObject o) {
        return OrderStatus.fromText(getString(o, STATUS).getOrNull());
    }

    private static BigDecimal extractAmount(JSONObject o) {
        BigDecimal amount;
        if (o.has(AMOUNT.id) && o.has(VALUE.id)) {
            amount = getBigDecimal(o, AMOUNT)
                .getOrElse(BigDecimal.ZERO);
        } else {
            amount = BigDecimal.ZERO;
        }
        return amount;
    }


    public static class DiffOrderException extends Exception{
        DiffOrderException(String message){
            super(message);
        }

        static DiffOrderException build(List<String> messages){
            return new DiffOrderException(String.join(",", messages));
        }
    }
}