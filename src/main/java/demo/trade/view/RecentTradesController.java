package demo.trade.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import lombok.NonNull;

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

}
