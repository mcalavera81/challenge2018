package demo.trade.view;

import javafx.beans.property.ListProperty;

public interface TradeViewPopulator {

    ListProperty<TradeTableRow> getTradeProp();

}
