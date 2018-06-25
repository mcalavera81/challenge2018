package demo.trade.source.parser;

import demo.support.helpers.WithId;
import demo.support.helpers.TransformHelpers;
import demo.trade.business.state.Trade;
import demo.trade.business.state.Trade.TradeType;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import org.json.JSONObject;

import java.util.List;

import static demo.support.parser.JsonParser.*;
import static demo.trade.source.parser.TradeParser.TradeField.*;


/*
{
"book": "btc_mxn",
"created_at": "2018-06-07T19:09:07+0000",
"amount": "0.00037379",
"maker_side": "buy",
"price": "154597.33",
"tid": 7730192

 */
public class TradeParser {

    public enum TradeField implements WithId {

        BOOK("book"),
        TIMESTAMP("created_at"),
        AMOUNT("amount"),
        SIDE("maker_side"),
        PRICE("price"),
        ID("tid");

        private final String id;
        TradeField(String id) { this.id = id; }
        public String id() { return this.id;}
    }

    public static Try<Trade> parse(JSONObject o) {

        Validation<TradeParserException, Trade> trade =
            TransformHelpers.getValidation(getDateFromString(o, TIMESTAMP))
                .combine(TransformHelpers.getValidation(getString(o, BOOK)))
                .combine(TransformHelpers.getValidation(getBigDecimal(o, AMOUNT)))
                .combine(TransformHelpers.getValidation(getString(o, SIDE).flatMap(TradeType::fromText)))
                .combine(TransformHelpers.getValidation(getBigDecimal(o, PRICE)))
                .combine(TransformHelpers.getValidation(getLong(o, ID)))
                .ap((timestamp, book, amount, side, price, id) ->
                    Trade.builder()
                            .book(book)
                            .timestamp(timestamp)
                            .type(side)
                            .id(id)
                            .price(price)
                            .amount(amount)
                            .build()

                ).mapError(error -> TradeParserException.build(error.asJava()));



        return TransformHelpers.toTry(trade);

    }




    public static class TradeParserException extends Exception{
        TradeParserException(String message){
            super(message);
        }

        static TradeParserException build(List<String> messages){
            return new TradeParserException(String.join(",", messages));
        }
    }
}
