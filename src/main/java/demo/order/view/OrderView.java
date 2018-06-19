package demo.order.view;

import demo.order.domain.Order;
import demo.order.domain.Order.Ask;
import demo.order.domain.Order.Bid;
import demo.order.parser.UtilParser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static demo.order.view.OrderView.OrderViewField.*;

@NoArgsConstructor
public class OrderView {

    public static class BidView extends OrderView {
        BidView(){}
        public static BidView build(Bid o){
            return OrderView.build(o, BidView.class);
        }
    }
    public static class AskView extends OrderView {
        AskView(){}
        public static AskView build(Ask o){
            return OrderView.build(o, AskView.class);
        }

    }

    public enum OrderViewField{
        ID("id","Id"),
        TIMESTAMP("timestamp","Timestamp"),
        PRICE("price","Price"),
        AMOUNT("amount","Amount"),
        VALUE("value","Value"),
        SUM("sum","Sum");

        private final String id;
        private final String name;

        OrderViewField(String id , String name){
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



    private static  <T extends OrderView> T build(Order o, Class<T> clazz) throws
        RuntimeException{
        try{
            final T ov = clazz.newInstance();

            ov.setId(o.getId());
            ov.setAmount(o.getAmount());
            ov.setPrice(o.getPrice());
            ov.setTimestamp(UtilParser.orderDateFormat(o.getTimestamp()));
            ov.setValue("");
            ov.setSum("");
            return ov;

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private StringProperty idProperty() { return id; }
    public String getId() { return idProperty().get(); }
    void setId(String id) { this.idProperty().set(id);}

    private StringProperty timestampProperty() { return timestamp; }
    public String getTimestamp() { return timestampProperty().get(); }
    void setTimestamp(String timestamp) {this.timestampProperty().set(timestamp);}

    private StringProperty priceProperty() { return price;}
    public String getPrice() { return priceProperty().get();}
    void setPrice(BigDecimal price) {
        priceProperty().set(price.toString());
    }

    private StringProperty amountProperty() { return amount; }
    public String getAmount() { return amountProperty().get();}
    void setAmount(BigDecimal amount) {
        amountProperty().set(amount.toString());
    }

    private StringProperty valueProperty() { return value;}
    public String getValue() { return value.get();}
    void setValue(String value) { valueProperty().set(value);}

    private StringProperty sumProperty() { return sum;}
    public String getSum() { return sum.get();}
    void setSum(String sum) { sumProperty().set(sum);}

}
