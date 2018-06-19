package demo.order.view;

import javafx.beans.property.ListProperty;

public interface OrderViewPopulator {
    ListProperty<OrderView.BidView> getBidsProp();

    ListProperty<OrderView.AskView> getAsksProp();
}
