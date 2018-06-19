package demo.shared;

import demo.order.view.OrderBookController;
import demo.trade.view.RecentTradesController;
import javafx.fxml.FXML;

public class MainController {


    @FXML
    private OrderBookController orderBookController;

    @FXML
    private RecentTradesController recentTradesController;

    @FXML
    public void initialize() {
    }

}
