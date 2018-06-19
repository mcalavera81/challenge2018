package demo.trade.domain;

import demo.shared.parser.UtilParser.BitsoBook;
import lombok.val;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

import static demo.TestUtils.*;
import static demo.trade.parser.TestTradeUtils.randSource;
import static demo.trade.parser.TestTradeUtils.randTradeId;
import static demo.trade.parser.TestTradeUtils.randType;

public class TradeTest {


    @Test
    public void test_builder_trade_by_default_is_real(){
        final val trade = Trade.builder()
            .book(BitsoBook.BTC_MXN.id())
            .amount(BigDecimal.valueOf(randAmount()))
            .price(BigDecimal.valueOf(randPrice()))
            .id(randTradeId())
            .timestamp(now())
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
                    .amount(BigDecimal.valueOf(randAmount()))
                    .price(BigDecimal.valueOf(randPrice()))
                    .id(randTradeId())
                    .timestamp(now())
                    .type(randType())
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(null)
                    .price(BigDecimal.valueOf(randPrice()))
                    .id(randTradeId())
                    .timestamp(now())
                    .type(randType())
                    .source(randSource())
                    .build());


        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(BigDecimal.valueOf(randAmount()))
                    .price(BigDecimal.valueOf(randPrice()))
                    .id(null)
                    .timestamp(now())
                    .type(randType())
                    .source(randSource())
                    .build());


        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(BigDecimal.valueOf(randAmount()))
                    .price(BigDecimal.valueOf(randPrice()))
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
                    .amount(BigDecimal.valueOf(randAmount()))
                    .price(BigDecimal.valueOf(randPrice()))
                    .id(randTradeId())
                    .timestamp(now())
                    .type(null)
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(BitsoBook.BTC_MXN.id())
                    .amount(BigDecimal.valueOf(randAmount()))
                    .price(BigDecimal.valueOf(randPrice()))
                    .id(randTradeId())
                    .timestamp(now())
                    .type(randType())
                    .source(null)
                    .build());

    }

}