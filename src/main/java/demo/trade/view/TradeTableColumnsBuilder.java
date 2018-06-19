package demo.trade.view;

import demo.trade.view.TradeTableRow.TradeViewField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.val;


class TradeTableColumnsBuilder {

    private static TableColumn<TradeTableRow, String> newColumn(
        TradeViewField field,
        TableView<TradeTableRow> table,
        double widthPercentage) {

        val column = new TableColumn<TradeTableRow,String>(field.getName());
        column.setCellValueFactory(new PropertyValueFactory<>(field.id()));
        column.setCellFactory(param -> new TableCell<TradeTableRow,String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                TradeTableRow tradeTableRow = (TradeTableRow) getTableRow().getItem();

                setText(null);
                setStyle(null);

                if (item != null ) {
                    setText(item);
                }

                if(tradeTableRow !=null){
                    if(tradeTableRow.getType().equals("buy")){
                        setStyle("-fx-background-color: yellow");
                    }else{
                        setStyle("-fx-background-color: red");
                    }
                }
            }
        });
        column.prefWidthProperty().bind(
            table.widthProperty().multiply(widthPercentage));
        return column;
    }

    /*
    col1.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        col2.prefWidthProperty().bind(table.widthProperty().multiply(0.7));

        col1.setResizable(false);
        col2.setResizable(false);
     */


    public static void setupColumns(TableView<TradeTableRow> table ){

        table.getColumns().setAll(
                //newColumn(TradeViewField.ID, table,0.1),
                newColumn(TradeViewField.TIMESTAMP,table, 0.25),
                newColumn(TradeViewField.PRICE,table,0.2),
                newColumn(TradeViewField.AMOUNT,table,0.2),
                newColumn(TradeViewField.VALUE,table,0.2)
                //newColumn(TradeViewField.TYPE,table,0.1),
                //newColumn(TradeViewField.BOOK,table,0.1)
        );

    }



}
