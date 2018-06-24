package demo.order.business.state;

import demo.order.source.poller.dto.OrderData;
import demo.shared.parser.UtilParser;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static demo.TestUtils.randAmount;
import static demo.TestUtils.randPrice;
import static demo.order.source.TestParserOrderUtils.randOrderId;

public class OrderDataTest {


    @Test
    public void test_constructor() {
        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(randOrderId())
                    .amount(null)
                    .price(UtilParser.bd(randPrice()))
                    .build());


        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(null)
                    .amount(UtilParser.bd(randAmount()))
                    .price(UtilParser.bd(randPrice()))
                    .build());



        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(randOrderId())
                    .amount(UtilParser.bd(randAmount()))
                    .price(null)
                    .build());
    }

}