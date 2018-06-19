package demo.order.domain;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

import static demo.TestUtils.randAmount;
import static demo.TestUtils.randPrice;
import static demo.order.parser.TestParserOrderUtils.randOrderId;

public class OrderDataTest {


    @Test
    public void test_constructor() {
        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(randOrderId())
                    .amount(null)
                    .price(BigDecimal.valueOf(randPrice()))
                    .build());


        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(null)
                    .amount(BigDecimal.valueOf(randAmount()))
                    .price(BigDecimal.valueOf(randPrice()))
                    .build());



        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(randOrderId())
                    .amount(BigDecimal.valueOf(randAmount()))
                    .price(null)
                    .build());
    }

}