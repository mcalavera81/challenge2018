package demo.order.source.stream.dto;

import demo.order.business.state.Order;
import demo.order.business.state.Order.Ask;
import demo.order.business.state.Order.Bid;
import demo.order.source.poller.dto.OrderData;
import demo.order.source.stream.parser.DiffOrderParser;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.String.format;


@Getter
@ToString
public class DiffOrder<T extends Order> {

    @NonNull
    private transient final OrderStatus status;

    @NonNull
    private final T order;

    public String getId(){
        return order.getId();
    }

    public BigDecimal getRate(){
        return order.getPrice();
    }

    public BigDecimal getAmount(){
        return order.getAmount();
    }

    public BigDecimal getValue(){
        return order.getAmount().multiply(order.getPrice());
    }

    public OrderType getType(){
        return order.getType();
    }

    public ZonedDateTime getTimestamp(){return order.getTimestamp();}


    public DiffOrder(
        OrderType type,
        OrderData data,
        ZonedDateTime timestamp,
        OrderStatus status) {

        this.status = status;
        this.order = Order.build(type, data, timestamp);

    }

    public static Try<DiffOrder> build(JSONObject json){
        return DiffOrderParser.parseDiffOrder(json);
    }

    public boolean isCancelledOrCompleted(){
        return order.getAmount().equals(BigDecimal.ZERO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiffOrder<?> diffOrder = (DiffOrder<?>) o;
        return Objects.equals(order, diffOrder.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), order);
    }

    public enum OrderStatus {
        OPEN("open"),CANCELLED("cancelled"),COMPLETED("completed");

        private final String name;

        OrderStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static OrderStatus fromText(String text) {
            return Arrays.stream(values())
                .filter(bl -> bl.name.equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
        }


    }

    public enum OrderType{

        BUY(0, Bid.class),SELL(1, Ask.class);

        private final int id;
        private final Class clazz;

        OrderType(int id, Class clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        public int getId() {
            return id;
        }

        public Class getClassType() {
            return clazz;
        }

        public static Try<OrderType> fromInt(Integer id) {
            return Arrays.stream(values())
                .filter(bl -> bl.id == id)
                .findFirst()
                .map(Try::success)
                .orElse(
                    Try.failure(
                        new RuntimeException(
                            format("Not valid order type: %s", id))));
        }
    }
}
