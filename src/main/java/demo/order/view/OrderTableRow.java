package demo.order.view;

import demo.order.business.state.Order;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static demo.order.view.OrderTableRow.OrderViewField.*;
import static demo.support.helpers.DateTimeHelpers.orderDateFormat;
import static demo.support.helpers.TransformHelpers.formatMajor;
import static demo.support.helpers.TransformHelpers.formatMinor;

@NoArgsConstructor
public class OrderTableRow {


    private final StringProperty id = prop(this, ID);
    private final StringProperty timestamp = prop(this, TIMESTAMP);
    private final StringProperty price = prop(this, PRICE);
    private final StringProperty amount = prop(this, AMOUNT);
    private final StringProperty value = prop(this, VALUE);
    private final StringProperty sum = prop(this, SUM);

    public static OrderTableRow build(Order o,
                                      BigDecimal sumAccumulated) {
        OrderTableRow row = new OrderTableRow();

        row.setId(o.getId());
        row.setAmount(o.getAmount());
        row.setPrice(o.getPrice());
        row.setTimestamp(o.getTimestamp());
        row.setValue(o.getValue());
        row.setSum(sumAccumulated);
        return row;
    }

    private StringProperty idProperty() {
        return id;
    }

    public String getId() {
        return idProperty().get();
    }

    void setId(String id) {
        this.idProperty().set(id);
    }

    private StringProperty timestampProperty() {
        return timestamp;
    }

    public String getTimestamp() {
        return timestampProperty().get();
    }

    void setTimestamp(ZonedDateTime timestamp) {
        this.timestampProperty().set(orderDateFormat(timestamp));
    }

    private StringProperty priceProperty() {
        return price;
    }

    public String getPrice() {
        return priceProperty().get();
    }

    void setPrice(BigDecimal price) {
        priceProperty().set(formatMinor(price));
    }

    private StringProperty amountProperty() {
        return amount;
    }

    public String getAmount() {
        return amountProperty().get();
    }

    void setAmount(BigDecimal amount) {
        amountProperty().set(formatMajor(amount));
    }

    private StringProperty valueProperty() {
        return value;
    }

    public String getValue() {
        return valueProperty().get();
    }

    public void setValue(BigDecimal value) {
        valueProperty().set(formatMinor(value));
    }

    private StringProperty sumProperty() {
        return sum;
    }

    public String getSum() {
        return sum.get();
    }

    void setSum(BigDecimal sum) {
        sumProperty().set(formatMajor(sum));
    }

    private SimpleStringProperty prop(Object bean, OrderViewField field){
        return new SimpleStringProperty(bean, field.id());
    }

    public enum OrderViewField {
        ID("id", "Id"),
        TIMESTAMP("timestamp", "Timestamp"),
        PRICE("price", "Price"),
        AMOUNT("amount", "Amount"),
        VALUE("value", "Value"),
        SUM("sum", "Sum");

        private final String id;
        private final String name;

        OrderViewField(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String id() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
