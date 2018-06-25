package demo.order.business.state;

import demo.order.source.stream.dto.DiffOrder.OrderType;
import demo.order.source.poller.dto.OrderData;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
//@ToString
public abstract class Order {


    public abstract OrderType getType();

    public static <T> T build(
        OrderType type,
        OrderData data,
        ZonedDateTime timestamp
    ){
        Class<T> clazz = type.getClassType();
        try {
            val constructor = clazz.getDeclaredConstructor(
                OrderData.class, ZonedDateTime.class);
            return constructor.newInstance(data, timestamp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @NonNull
    private OrderData data;

    @NonNull
    private ZonedDateTime timestamp;

    public String getId(){
        return data.getId();
    }

    public BigDecimal getPrice(){
        return data.getPrice();
    }

    public BigDecimal getAmount(){
        return data.getAmount();
    }

    public BigDecimal getValue(){
        return data.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(data, order.data) &&
            Objects.equals(getType(), order.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, getType());
    }

    public static class Bid extends Order{
        public Bid(OrderData data, ZonedDateTime dt) {
            super(data, dt);
        }

        @Override
        public OrderType getType() {
            return OrderType.BUY;
        }

    }

    public static class Ask extends Order{
        public Ask(OrderData data, ZonedDateTime dt) {
            super(data, dt);
        }


        @Override
        public OrderType getType() {
            return OrderType.SELL;
        }

    }

}
