package demo.order.domain;

import demo.order.parser.OrderDataParser;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;

import java.math.BigDecimal;

@Builder
@Getter
public class OrderData {

    @NonNull
    private String id;

    @NonNull
    private BigDecimal price;

    @NonNull
    private BigDecimal amount;

    public static Try<OrderData> build(JSONObject json){
        return OrderDataParser.parseOrderData(json);
    }
}
