package demo.trade.parser;

import demo.order.parser.UtilParser.BitsoBook;
import demo.trade.domain.Trade;
import demo.trade.domain.Trade.TradeSource;
import demo.trade.domain.Trade.TradeType;
import demo.trade.domain.TradesBatch;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import lombok.val;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static demo.TestUtils.*;
import static demo.order.parser.UtilParser.tradeDateFormat;
import static demo.trade.parser.TradeParser.TradeField.*;

public class TestTradeUtils {

    private static final Random rand = new Random();

    public static Trade[] newTrades(int num, int... idx){
        int startIndex = idx!=null && idx.length==1? idx[0]:0;

        Trade[] trades = Stream
            .range(startIndex, num+startIndex)
            .zipWithIndex()
            .map(tid -> Tuple.of(tid._2, randTrade(tid._1)))
            .foldLeft(
                new Trade[num],
                (array, indexTrade) -> {
                    array[indexTrade._1]=indexTrade._2;
                    return array;
                }
            );

        return trades;

    }

    private static Trade randTrade(long... id){
        long tid = id!=null && id.length==1? id[0]:randTradeId();

        return Trade.builder()
            .book(BitsoBook.BTC_MXN.id())
            .amount(BigDecimal.valueOf(randAmount()))
            .price(BigDecimal.valueOf(randPrice()))
            .id(tid)
            .timestamp(now())
            .type(randType())
            .build();
    }

    public static Tuple2<JSONObject,Trade> jsonTrade(){

        val t = randTrade();

        JSONObject o =new JSONObject();
        o.put(BOOK.id(),t.getBook());
        o.put(TIMESTAMP.id(), tradeDateFormat(t.getTimestamp()));
        o.put(AMOUNT.id(),t.getAmount().toString());
        o.put(PRICE.id(),t.getPrice().toString());
        o.put(SIDE.id(),t.getType().getName());
        o.put(ID.id(),t.getId());

        return Tuple.of(o,t);

    }


    public static Long randTradeId(){
        return rand.nextLong();
    }

    public static TradeSource randSource(){
        return  rand.nextFloat()<0.5?
            TradeSource.REAL:
            TradeSource.SIMULATED;
    }

    public static TradeType randType(){
        return  rand.nextFloat()<0.5?TradeType.BUY:TradeType.SELL;
    }

    public static Try<TradesBatch> randTrades(int size, int maxId){
        val trades = new Trade[size];

        for (int i = 0, id =maxId; i < trades.length; i++,id--) {
            trades[i] = randTrade(id);
        }

        return Try.success(new TradesBatch(trades));
    }


    public static List<Trade> randTrades(int size){

        return Stream.range(0, size)
            .foldLeft(
                new ArrayList<Trade>(),
                (list, i) -> {
                    list.add(randTrade());
                    return list;
                });
    }

}
