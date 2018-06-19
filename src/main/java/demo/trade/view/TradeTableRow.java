package demo.trade.view;

import demo.order.parser.UtilParser;
import demo.trade.domain.Trade;
import demo.trade.domain.Trade.TradeSource;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static demo.trade.view.TradeTableRow.TradeViewField.*;

@NoArgsConstructor
public class TradeTableRow {


    public enum TradeViewField{
        ID("id","Id"),
        TIMESTAMP("timestamp","Timestamp"),
        PRICE("price","Price"),
        AMOUNT("amount","Amount"),
        VALUE("value","Value"),
        TYPE("type","Type"),
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

    public static TradeTableRow build(@NonNull Trade trade){
        TradeTableRow row = new TradeTableRow();
        row.setId(trade.getId().toString());
        row.setBook(trade.getBook());
        row.setTimestamp(UtilParser.orderDateFormat(trade.getTimestamp()));
        row.setAmount(trade.getAmount().toString());
        row.setPrice(trade.getPrice().toString());
        row.setType(trade.getType().getName());
        row.setSource(trade.getSource()== TradeSource.REAL?"Real":"Simulated");
        return row;
    }


    private StringProperty idProperty() { return id; }
    public String getId() { return idProperty().get(); }
    private void setId(String id) { this.idProperty().set(id);}

    private StringProperty timestampProperty() { return timestamp; }
    public String getTimestamp() { return timestampProperty().get(); }
    private void setTimestamp(String timestamp) {this.timestampProperty().set(timestamp);}

    private StringProperty priceProperty() { return price;}
    public String getPrice() { return priceProperty().get();}
    private void setPrice(String price) { priceProperty().set(price);}

    private StringProperty amountProperty() { return amount; }
    public String getAmount() { return amountProperty().get();}
    private void setAmount(String amount) { amountProperty().set(amount);}

    private StringProperty valueProperty() { return value; }
    public String getValue() { return valueProperty().get();}
    public void setValue(String value) { valueProperty().set(value);}

    private StringProperty typeProperty() { return type; }
    public String getType() { return typeProperty().get();}
    private void setType(String type) { typeProperty().set(type);}

    private StringProperty sourceProperty() { return source; }
    public String getSource() { return sourceProperty().get();}
    private void setSource(String source) { sourceProperty().set(source);}

    private StringProperty bookProperty() { return book; }
    public String getBook() { return bookProperty().get();}
    private void setBook(String book) { this.bookProperty().set(book);}
}
