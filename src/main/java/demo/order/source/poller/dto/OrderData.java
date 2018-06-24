package demo.order.source.poller.dto;

import demo.order.source.poller.parser.OrderDataParser;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Objects;

@Builder
@Getter
@ToString
public class OrderData {

    @NonNull
    private String id;

    @NonNull
    private BigDecimal price;

    @NonNull
    private BigDecimal amount;

    public BigDecimal getValue(){
        return price.multiply(amount);
    }

    public static Try<OrderData> build(JSONObject json){
        return OrderDataParser.parseOrderData(json);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderData orderData = (OrderData) o;
        return Objects.equals(id, orderData.id) &&
            Objects.equals(price, orderData.price) &&
            Objects.equals(amount, orderData.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, amount);
    }
}
