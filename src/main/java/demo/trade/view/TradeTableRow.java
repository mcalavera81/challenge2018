package demo.trade.view;

import demo.shared.formatter.UtilFormatter;
import demo.trade.business.state.Trade;
import demo.trade.business.state.Trade.TradeSource;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;

import static demo.trade.view.TradeTableRow.TradeViewField.*;

@NoArgsConstructor
public class TradeTableRow {


    private final static DecimalFormat minorFormat =
        new DecimalFormat("0.00");


    private final static DecimalFormat majorFormat =
        new DecimalFormat("0.00000000");

    public enum TradeRowType{
        UNDEFINED("UNDEFINED"),
        UP_TICK("UP TICK"),
        DOWN_TICK("DOWN TICK"),
        ZERO_TICK("ZERO TICK"),
        SIMULATED_SELL("SELL"),
        SIMULATED_BUY("BUY ORDER");

        private final String name;

        TradeRowType(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum TradeViewField{
        ID("id","Id"),
        TIMESTAMP("timestamp","Timestamp"),
        PRICE("price","Price"),
        AMOUNT("amount","Amount"),
        VALUE("value","Value"),
        TYPE("type","Type"), //UP, DOWN, SIMULATED
        SOURCE("source","Source"),
        BOOK("book","Book");

        private final String id;
        private final String name;

        TradeViewField(String id , String name){
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
    private final StringProperty type = new SimpleStringProperty(this, TYPE.id());
    private final StringProperty source = new SimpleStringProperty(this,SOURCE.id());
    private final StringProperty book = new SimpleStringProperty(this, BOOK.id());
    private TradeRowType enumType;


    public static TradeTableRow build(@NonNull Trade trade, TradeRowType type){
        TradeTableRow row = new TradeTableRow();
        row.setId(trade.getId().toString());
        row.setBook(trade.getBook());
        row.setTimestamp(trade.getTimestamp());
        row.setAmount(trade.getAmount());
        row.setPrice(trade.getPrice());
        row.setValue(trade.getValue());
        row.setType(type);
        row.setSource(trade.getSource()== TradeSource.REAL?"Real":"Simulated");
        return row;
    }


    private StringProperty idProperty() { return id; }
    public String getId() { return idProperty().get(); }
    private void setId(String id) { this.idProperty().set(id);}

    private StringProperty timestampProperty() { return timestamp; }
    public String getTimestamp() { return timestampProperty().get(); }
    private void setTimestamp(ZonedDateTime timestamp) {
        this.timestampProperty().set(UtilFormatter.orderDateFormat(timestamp));
    }

    private StringProperty priceProperty() { return price;}
    public String getPrice() { return priceProperty().get();}
    private void setPrice(BigDecimal price) {
        priceProperty().set(minorFormat.format(price));
    }

    private StringProperty amountProperty() { return amount; }
    public String getAmount() { return amountProperty().get();}
    private void setAmount(BigDecimal amount) {
        amountProperty().set(majorFormat.format(amount));}

    private StringProperty valueProperty() { return value; }
    public String getValue() { return valueProperty().get();}
    public void setValue(BigDecimal value) {
        valueProperty().set(minorFormat.format(value));}

    private StringProperty typeProperty() { return type; }
    public String getType() { return type.get(); }
    private void setType(TradeRowType type) {
        this.enumType = type;
        typeProperty().set(type.getName());
    }
    public TradeRowType getEnumType() {
        return enumType;
    }

    private StringProperty sourceProperty() { return source; }
    public String getSource() { return sourceProperty().get();}
    private void setSource(String source) { sourceProperty().set(source);}

    private StringProperty bookProperty() { return book; }
    public String getBook() { return bookProperty().get();}
    private void setBook(String book) { this.bookProperty().set(book);}
}
