package demo.order.source;

import demo.order.business.state.Order.Ask;
import demo.order.business.state.Order.Bid;
import demo.order.source.poller.dto.OrderData;
import demo.order.source.stream.dto.DiffOrder;
import demo.order.source.stream.dto.DiffOrder.OrderStatus;
import demo.order.source.stream.dto.DiffOrder.OrderType;
import demo.order.source.stream.dto.DiffOrdersBatch;
import demo.order.source.poller.parser.OrderBookSnaphsotParser;
import demo.order.source.stream.parser.DiffOrdersBatchParser;
import demo.shared.parser.TestParserUtils;
import demo.shared.parser.UtilParser;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static demo.TestUtils.randAmount;
import static demo.TestUtils.randPrice;
import static demo.order.source.stream.parser.DiffOrderParser.DiffOrderField.*;
import static demo.order.source.stream.parser.DiffOrdersBatchParser.DIFF_ORDERS;
import static demo.order.source.stream.dto.DiffOrder.OrderType.SELL;
import static demo.shared.parser.UtilParser.BitsoBook.BTC_MXN;
import static demo.shared.parser.UtilParser.RestResponseField.PAYLOAD;


public class TestParserOrderUtils {

    private static final Random rand = new Random();
    private static final int STR_LENGTH = 10;


    public static Tuple2<JSONObject,DiffOrder> jsonDiffOrder(
        String id,
        OrderType type){

        DiffOrder d = diffOrder(id, type, randPrice());

        JSONObject o =new JSONObject();

        o.put(TIMESTAMP.id(), d.getTimestamp().toInstant().toEpochMilli());
        o.put(RATE.id(), d.getRate().toString());
        o.put(AMOUNT.id(), d.getAmount().toString());
        o.put(TYPE.id(), d.getType().getId());
        o.put(ORDER_ID.id(), d.getId());
        o.put(STATUS.id(), OrderStatus.OPEN.getName());
        o.put(VALUE.id(), d.getAmount().multiply(d.getRate()).toString());

        return Tuple.of(o, d);

    }

    public static Tuple2<JSONObject,DiffOrder> jsonDiffOrder(
        String... id) {
        String orderId = (id!=null && id.length==1)?id[0]:randOrderId();
        return jsonDiffOrder(orderId, randType());
    }



    public static DiffOrdersBatch batchCancelled(
        long sequence,
        String id,
        OrderType type){

        DiffOrder diffOrder = diffOrderCancelled(id, type);
        return new DiffOrdersBatch(sequence,new DiffOrder[]{diffOrder});
    }

    public static DiffOrdersBatch batch(
        long sequence,
        String id,
        OrderType type,
        double price){

        DiffOrder diffOrder = diffOrder(id, type, price);
        return new DiffOrdersBatch(sequence,new DiffOrder[]{diffOrder});
    }

    private static DiffOrder diffOrderCancelled(String id, OrderType type) {
        OrderData data  =OrderData.builder()
            .id(id)
            .amount(BigDecimal.ZERO)
            .price(UtilParser.bd(randPrice()))
            .build();

        return new DiffOrder(type,data, UtilParser.now(),randStatus());
    }

    private static DiffOrder diffOrder(String id, OrderType type, double price) {
        OrderData data  =OrderData.builder()
            .id(id)
            .amount(UtilParser.bd(randAmount()))
            .price(UtilParser.bd(price))
            .build();

        return new DiffOrder(type,data, UtilParser.now(),randStatus());
    }

    private static OrderStatus randStatus(){
        switch (rand.nextInt(2)){
            case 0:
                return OrderStatus.OPEN;
            case 1:
                return OrderStatus.CANCELLED;
            default:
                return OrderStatus.COMPLETED;
        }
    }

    private static OrderType randType(){
        return rand.nextFloat()<0.5?OrderType.BUY: SELL;
    }

    public static String randOrderId(){
        return rand.ints(48,122)
                .filter(i-> (i < 58) || (i > 64 && i < 91) || (i > 96))
                .mapToObj(i -> (char) i)
                .limit(STR_LENGTH)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }


    public static JSONObject orderBatch(
        OrderType type,long sequence,String orderId){


        Tuple2<JSONObject, DiffOrder> diffOrder = jsonDiffOrder(orderId,type);

        return new JSONObject()
            .put(DiffOrdersBatchParser.DiffOrdersBatchField.CHANNEL.id(), DIFF_ORDERS)
            .put(DiffOrdersBatchParser.DiffOrdersBatchField.BOOK.id(), BTC_MXN.id())
            .put(OrderBookSnaphsotParser.OrderBookField.BOOK_SEQUENCE.id(), sequence)
            .put(PAYLOAD.id(), Collections.singletonList(diffOrder._1));
    }

    public static JSONObject orderBatch(Option<Collection>
                                                     payload, Option<Long> sequence){

        JSONObject update = new JSONObject()
                .put(DiffOrdersBatchParser.DiffOrdersBatchField.CHANNEL.id(), DIFF_ORDERS)
                .put(DiffOrdersBatchParser.DiffOrdersBatchField.BOOK.id(), BTC_MXN.id());

        sequence.forEach($->update.put(OrderBookSnaphsotParser.OrderBookField.BOOK_SEQUENCE.id(), $));
        payload.forEach($->update.put(PAYLOAD.id(), $));
        return update;
    }

    public static JSONObject book(Long sequence,
                            String timestamp,
                            Option<Collection> bids,
                            Option<Collection> asks){


        return TestParserUtils.responseWithPayload(()->{
            JSONObject book =new JSONObject()
                    .put(OrderBookSnaphsotParser.OrderBookField.BOOK_SEQUENCE.id(), sequence)
                    .put(OrderBookSnaphsotParser.OrderBookField.UPDATED_AT.id(), timestamp);
            bids.map($-> book.put(OrderBookSnaphsotParser.OrderBookField.BIDS.id(), $));
            asks.map($-> book.put(OrderBookSnaphsotParser.OrderBookField.ASKS.id(), $));
            return book;
        });

    }

    public static JSONObject order(Double price, Double amount, String oid){
        return new JSONObject().
                put("book","btc_mxn").
                put("price", price==null?null:Double.toString(price)).
                put("amount",amount==null?null:Double.toString(amount)).
                put("oid",oid);
    }

    public static OrderData randOrderData(){
        return OrderData.builder()
            .id(randOrderId())
            .amount(UtilParser.bd(randAmount()))
            .price(UtilParser.bd(randPrice()))
            .build();

    }


    public static Bid randBid(){
        return new Bid(randOrderData(), UtilParser.now());
    }

    public static Ask randAsk(){
        return new Ask(randOrderData(), UtilParser.now());
    }


    public static String id(String id){
        return id;
    }
}
