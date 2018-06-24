package demo.order.view;

import javafx.beans.property.ListProperty;

public interface OrderViewPopulator {
    ListProperty<OrderTableRow> getBidsProp();

    ListProperty<OrderTableRow> getAsksProp();
}
