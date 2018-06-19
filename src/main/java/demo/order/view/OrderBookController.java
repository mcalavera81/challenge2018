package demo.order.view;


import demo.order.view.OrderView.AskView;
import demo.order.view.OrderView.BidView;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import lombok.NonNull;

public class OrderBookController {

    @FXML
    private TableView<BidView> bids;
    @FXML
    private TableView<AskView> asks;


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


}
