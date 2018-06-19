package demo.order.view;

import demo.order.view.OrderView.OrderViewField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.val;


class OrderBookColumnsBuilder {

    private static <T extends OrderView> TableColumn<T, String> newColumn(
        OrderViewField field) {

        val column = new TableColumn<T, String>(field.getName());
        column.setCellValueFactory(new PropertyValueFactory<>(field.id()));
        return column;
    }

    public static void setupColumns(TableView<? extends OrderView> table ){


        table.getColumns().setAll(
                //newColumn(OrderViewField.ID),
                //newColumn(OrderViewField.TIMESTAMP),
                newColumn(OrderViewField.PRICE),
                newColumn(OrderViewField.AMOUNT),
                newColumn(OrderViewField.VALUE),
                newColumn(OrderViewField.SUM)
        );

    }



}
