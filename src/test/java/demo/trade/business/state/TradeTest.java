package demo.trade.business.state;

import demo.app.Constants;
import demo.support.helpers.DateTimeHelpers;
import demo.support.helpers.TransformHelpers;
import lombok.val;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static demo.TestHelpers.randAmount;
import static demo.TestHelpers.randPrice;
import static demo.trade.TestTradeHelpers.*;

public class TradeTest {


    @Test
    public void test_builder_trade_by_default_is_real(){
        final val trade = Trade.builder()
            .book(Constants.BitsoBook.BTC_MXN.id())
            .amount(TransformHelpers.bd(randAmount()))
            .price(TransformHelpers.bd(randPrice()))
            .id(randTradeId())
            .timestamp(DateTimeHelpers.now())
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
                    .amount(TransformHelpers.bd(randAmount()))
                    .price(TransformHelpers.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(DateTimeHelpers.now())
                    .type(randType())
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(Constants.BitsoBook.BTC_MXN.id())
                    .amount(null)
                    .price(TransformHelpers.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(DateTimeHelpers.now())
                    .type(randType())
                    .source(randSource())
                    .build());


        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(Constants.BitsoBook.BTC_MXN.id())
                    .amount(TransformHelpers.bd(randAmount()))
                    .price(TransformHelpers.bd(randPrice()))
                    .id(null)
                    .timestamp(DateTimeHelpers.now())
                    .type(randType())
                    .source(randSource())
                    .build());


        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(Constants.BitsoBook.BTC_MXN.id())
                    .amount(TransformHelpers.bd(randAmount()))
                    .price(TransformHelpers.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(null)
                    .type(randType())
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(Constants.BitsoBook.BTC_MXN.id())
                    .amount(TransformHelpers.bd(randAmount()))
                    .price(TransformHelpers.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(DateTimeHelpers.now())
                    .type(null)
                    .source(randSource())
                    .build());

        Assertions.assertThrows(
            NullPointerException.class,
            ()->
                Trade.builder()
                    .book(Constants.BitsoBook.BTC_MXN.id())
                    .amount(TransformHelpers.bd(randAmount()))
                    .price(TransformHelpers.bd(randPrice()))
                    .id(randTradeId())
                    .timestamp(DateTimeHelpers.now())
                    .type(randType())
                    .source(null)
                    .build());

    }

}