package demo.order.view;

import demo.order.business.state.Order;
import demo.shared.formatter.UtilFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static demo.order.view.OrderTableRow.OrderViewField.*;

@NoArgsConstructor
public class OrderTableRow {


    private final static DecimalFormat minorFormat =
        new DecimalFormat("0.00");


    private final static DecimalFormat majorFormat =
        new DecimalFormat("0.00000000");

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

    private final StringProperty id = new SimpleStringProperty(this, ID.id());
    private final StringProperty timestamp = new SimpleStringProperty(this, TIMESTAMP.id());
    private final StringProperty price = new SimpleStringProperty(this, PRICE.id());
    private final StringProperty amount = new SimpleStringProperty(this, AMOUNT.id());
    private final StringProperty value = new SimpleStringProperty(this, VALUE.id());
    private final StringProperty sum = new SimpleStringProperty(this, SUM.id());


    public static OrderTableRow build(Order o,
                                       BigDecimal sumAccumulated) {
        OrderTableRow row = new OrderTableRow();

        row.setId(o.getId());
        row.setAmount(o.getAmount());
        row.setPrice(o.getPrice());
        row.setTimestamp(UtilFormatter.orderDateFormat(o.getTimestamp()));
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

    void setTimestamp(String timestamp) {
        this.timestampProperty().set(timestamp);
    }

    private StringProperty priceProperty() {
        return price;
    }

    public String getPrice() {
        return priceProperty().get();
    }

    void setPrice(BigDecimal price) {
        priceProperty().set(minorFormat.format(price));
    }

    private StringProperty amountProperty() {
        return amount;
    }

    public String getAmount() {
        return amountProperty().get();
    }

    void setAmount(BigDecimal amount) {
        amountProperty().set(majorFormat.format(amount));
    }

    private StringProperty valueProperty() {
        return value;
    }

    public String getValue() {
        return valueProperty().get();
    }

    public void setValue(BigDecimal value) {
        valueProperty().set(minorFormat.format(value));
    }

    private StringProperty sumProperty() {
        return sum;
    }

    public String getSum() {
        return sum.get();
    }

    void setSum(BigDecimal sum) {
        sumProperty().set(majorFormat.format(sum));
    }


}
