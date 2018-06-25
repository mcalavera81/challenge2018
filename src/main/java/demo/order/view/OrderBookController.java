package demo.order.view;


import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.NonNull;
import lombok.val;

public class OrderBookController {

    @FXML
    private TableView<OrderTableRow> bids;
    @FXML
    private TableView<OrderTableRow> asks;


    private final OrderViewPopulator bookProps;

    public OrderBookController(@NonNull OrderViewPopulator bookProps){
        this.bookProps = bookProps;
    }


    @FXML
    public void initialize() {
        OrderBookColumnsBuilder.setupColumns(bids);
        OrderBookColumnsBuilder.setupColumns(asks);

        bids.itemsProperty().bind(bookProps.getBidsProp());
        asks.itemsProperty().bind(bookProps.getAsksProp());


    }


    static class OrderBookColumnsBuilder {

        private static TableColumn<OrderTableRow, String> newColumn(
            OrderTableRow.OrderViewField field,
            TableView<OrderTableRow> table,
            double widthPercentage) {

            val column = new TableColumn<OrderTableRow,String>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.id()));
            column.prefWidthProperty().bind(
                table.widthProperty().multiply(widthPercentage));
            column.setResizable(false);
            return column;
        }

        public static void setupColumns(TableView<OrderTableRow>
                                            table ){


            table.getColumns().setAll(
                newColumn(OrderTableRow.OrderViewField.PRICE,table,0.25),
                newColumn(OrderTableRow.OrderViewField.AMOUNT,table,0.25),
                newColumn(OrderTableRow.OrderViewField.VALUE,table,0.25),
                newColumn(OrderTableRow.OrderViewField.SUM,table,0.25)

            );

        }



    }
}
