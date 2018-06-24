package demo.trade.parser;

import demo.shared.parser.UtilParser;
import demo.shared.parser.UtilParser.BitsoBook;
import demo.trade.business.state.Trade;
import demo.trade.business.state.Trade.TradeSource;
import demo.trade.business.state.Trade.TradeType;
import demo.trade.source.dto.TradesBatch;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import lombok.val;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static demo.TestUtils.randAmount;
import static demo.TestUtils.randPrice;
import static demo.shared.formatter.UtilFormatter.tradeDateFormat;
import static demo.trade.source.parser.TradeParser.TradeField.*;

public class TestTradeUtils {

    private static final Random rand = new Random();


    private static Trade randTradeWithPrice(double price) {

        return Trade.builder()
            .book(BitsoBook.BTC_MXN.id())
            .amount(UtilParser.bd(randAmount()))
            .price(UtilParser.bd(price))
            .id(randTradeId())
            .timestamp(UtilParser.now())
            .type(randType())
            .build();

    }
    private static Trade randTrade(long... id){
        long tid = id!=null && id.length==1? id[0]:randTradeId();

        return Trade.builder()
            .book(BitsoBook.BTC_MXN.id())
            .amount(UtilParser.bd(randAmount()))
            .price(UtilParser.bd(randPrice()))
            .id(tid)
            .timestamp(UtilParser.now())
            .type(randType())
            .source(randSource())
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


    public static Try<TradesBatch> randTradesBatch(int size, int maxId){
        List<Trade> trades = new ArrayList<>(size);
        for (int i = 0, id = maxId; i < size; i++,id--) {
            trades.add(randTrade(id));
        }

        return Try.success(new TradesBatch(trades));
    }


    public static List<Trade> randTradesWithPrices(double... prices){


        return Stream.ofAll(prices)
            .foldLeft(
                new ArrayList<Trade>(),
                (list, price) -> {
                    list.add(randTradeWithPrice(price));
                    return list;
                });
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
