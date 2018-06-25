package demo.order.business.state;

import demo.order.source.poller.dto.OrderData;
import demo.support.helpers.TransformHelpers;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static demo.TestHelpers.randAmount;
import static demo.TestHelpers.randPrice;
import static demo.order.TestOrderHelpers.randOrderId;

public class OrderDataTest {


    @Test
    public void test_constructor() {
        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(randOrderId())
                    .amount(null)
                    .price(TransformHelpers.bd(randPrice()))
                    .build());


        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(null)
                    .amount(TransformHelpers.bd(randAmount()))
                    .price(TransformHelpers.bd(randPrice()))
                    .build());



        Assertions.assertThrows(NullPointerException.class,
            () ->
                OrderData.builder()
                    .id(randOrderId())
                    .amount(TransformHelpers.bd(randAmount()))
                    .price(null)
                    .build());
    }

}