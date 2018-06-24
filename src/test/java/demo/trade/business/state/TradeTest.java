package demo.trade.business.state;

import demo.shared.parser.UtilParser;
import demo.shared.parser.UtilParser.BitsoBook;
import lombok.val;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static demo.TestUtils.randAmount;
import static demo.TestUtils.randPrice;
import static demo.trade.parser.TestTradeUtils.*;

public class TradeTest {


    @Test
    public void test_builder_trade_by_default_is_real(){
        final val trade = Trade.builder()
            .book(BitsoBook.BTC_MXN.id())
            .amount(UtilParser.bd(randAmount()))
            .price(UtilParser.bd(randPrice()))
            .id(randTradeId())
            .timestamp(UtilParser.now())
            .type(randType())
            .build();

        Assertions.assertEquals(Trade.TradeSource.REAL,trade.getSource());

    }

    @Test
    public void test_builder_non_null(){

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(null)
                    .amount(UtilParser.bd(randAmount()))
                    .price(UtilParser.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(UtilParser.now())
                    .type(randType())
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(null)
                    .price(UtilParser.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(UtilParser.now())
                    .type(randType())
                    .source(randSource())
                    .build());


        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(UtilParser.bd(randAmount()))
                    .price(UtilParser.bd(randPrice()))
                    .id(null)
                    .timestamp(UtilParser.now())
                    .type(randType())
                    .source(randSource())
                    .build());


        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(UtilParser.bd(randAmount()))
                    .price(UtilParser.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(null)
                    .type(randType())
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(UtilParser.bd(randAmount()))
                    .price(UtilParser.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(UtilParser.now())
                    .type(null)
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(UtilParser.bd(randAmount()))
                    .price(UtilParser.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(UtilParser.now())
                    .type(randType())
                    .source(null)
                    .build());

    }

}