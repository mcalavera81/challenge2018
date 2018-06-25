package demo.trade.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.NonNull;
import lombok.val;

public class RecentTradesController {

    private final TradeViewPopulator populator;


    public RecentTradesController(@NonNull TradeViewPopulator populator){
        this.populator = populator;
    }

    @FXML
    private TableView<TradeTableRow> tradeTable;


    @FXML
    public void initialize() {
        TradeTableColumnsBuilder.setupColumns(tradeTable);
        tradeTable.itemsProperty().bind(populator.getTradeProp());
    }

    static class TradeTableColumnsBuilder {

        private static TableColumn<TradeTableRow, String> newColumn(
            TradeTableRow.TradeViewField field,
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
                        switch (tradeTableRow.getEnumType()) {
                            case UP_TICK:
                                if(getTableColumn().getText().equals
                                    (TradeTableRow.TradeViewField.SOURCE.getName()))
                                    setStyle("-fx-background-color: palegreen");
                                break;
                            case DOWN_TICK:
                                if(getTableColumn().getText().equals
                                    (TradeTableRow.TradeViewField.SOURCE.getName()))
                                    setStyle("-fx-background-color: tomato");
                                break;
                            case SIMULATED_BUY:
                                if(getTableColumn().getText().equals
                                    (TradeTableRow.TradeViewField.TYPE.getName()))
                                    setStyle("-fx-background-color: turquoise");
                                break;
                            case SIMULATED_SELL:
                                if(getTableColumn().getText().equals
                                    (TradeTableRow.TradeViewField.TYPE.getName()))
                                    setStyle("-fx-background-color: lightblue");
                                break;
                            case ZERO_TICK:
                                setStyle("-fx-background-color: white");
                                break;
                        }

                    }
                }
            });
            column.prefWidthProperty().bind(
                table.widthProperty().multiply(widthPercentage));
            column.setResizable(false);
            return column;
        }


        public static void setupColumns(TableView<TradeTableRow> table ){

            table.getColumns().setAll(
                    newColumn(TradeTableRow.TradeViewField.TIMESTAMP,table, 0.2),
                    newColumn(TradeTableRow.TradeViewField.PRICE,table,0.1),
                    newColumn(TradeTableRow.TradeViewField.AMOUNT,table,0.1),
                    newColumn(TradeTableRow.TradeViewField.VALUE,table,0.1),
                    newColumn(TradeTableRow.TradeViewField.SOURCE,table,0.1),
                    newColumn(TradeTableRow.TradeViewField.TYPE,table,0.1)
            );

        }



    }
}
