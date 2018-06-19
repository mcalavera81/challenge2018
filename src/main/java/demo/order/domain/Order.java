package demo.order.domain;

import demo.order.service.websocket.DiffOrder.OrderType;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
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


    @EqualsAndHashCode
    public static class Bid extends Order{
        public Bid(OrderData data, ZonedDateTime dt) {
            super(data, dt);
        }

        @Override
        public OrderType getType() {
            return OrderType.BUY;
        }
    }

    @EqualsAndHashCode
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
