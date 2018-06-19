package demo.view;

import demo.Backend;
import demo.Frontend;
import demo.FrontendJavaFx;
import demo.order.domain.OrderBook;
import demo.trade.domain.RecentTradesLog;
import demo.order.view.OrderView.OrderViewField;
import demo.shared.config.AppConfiguration;
import demo.trade.view.TradeTableRow.TradeViewField;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import lombok.val;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Arrays;

import static demo.order.parser.TestOrderUtils.randAsk;
import static demo.order.parser.TestOrderUtils.randBid;
import static demo.trade.parser.TestTradeUtils.randTrades;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;

public class UiTest extends ApplicationTest {

    private static final String ORDER_BOOK_CONTAINER_ID = "#order_book";
    private static final String RECENT_TRADES_CONTAINER_ID = "#recent_trades";
    private static final String BIDS_TABLE_ID = "#bids";
    private static final String ASKS_TABLE_ID = "#asks";
    private static final String TRADES_TABLE_ID = "#tradeTable";


    private static Frontend frontend;

    @BeforeClass
    public static void beforeClass() throws Exception {

        // --------------- GIVEN ------------------------------

        FxToolkit.registerPrimaryStage();
        AppConfiguration conf = AppConfiguration.tryBuild();

        final Backend backend = mock(Backend.class,RETURNS_DEEP_STUBS);
        ///val backend = mock(Backend.class);
        val orderBook = mock(OrderBook.class);
        val tradesLog = mock(RecentTradesLog.class);

        //Mockito.when(backend.getOrderBook()).thenReturn(orderBook);
        //Mockito.when(backend.getRecentTradesLog()).thenReturn(tradesLog);

        Mockito.when(backend.getOrderBook().getAsks(any()))
            .thenReturn(Arrays.asList(randAsk()));
        Mockito.when(backend.getOrderBook().getBids(any()))
            .thenReturn(
                Arrays.asList(
                    randBid(),
                    randBid()));

        Mockito.when(backend
            .getRecentTradesLog()
            .getRecentTrades(anyInt()))
            .thenReturn(randTrades(3));

        frontend = FrontendJavaFx.of(conf.getFrontendConfig(), backend);
        frontend.start();

        FxToolkit.setupScene(frontend::buildScene);
        FxToolkit.showStage();

        Thread.sleep(1000);

    }

    @AfterClass
    public static void afterClass() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_order_book_table() {

        val orderBook = lookup(ORDER_BOOK_CONTAINER_ID).queryAs(AnchorPane.class);
        val bidsTable = lookup(BIDS_TABLE_ID).queryAs(TableView.class);
        val asksTable = lookup(ASKS_TABLE_ID).queryAs(TableView.class);

        Assert.assertNotNull(orderBook);
        assertThat(orderBook).hasExactlyNumChildren(3);
        assertThat(orderBook).hasChild(BIDS_TABLE_ID);
        assertThat(orderBook).hasChild(ASKS_TABLE_ID);

        verifyThat(bidsTable,hasNumRows(2));
        verifyThat(asksTable,hasNumRows(1));

        assertBookOrderColumnNames(bidsTable.getColumns());
        assertBookOrderColumnNames(asksTable.getColumns());

    }

    @Test
    public void test_trade_table() {

        val tradesContainer = lookup(RECENT_TRADES_CONTAINER_ID).queryAs
            (AnchorPane.class);
        val tradesTable = lookup(TRADES_TABLE_ID).queryAs(TableView.class);

        Assert.assertNotNull(tradesContainer);
        assertThat(tradesContainer).hasExactlyNumChildren(2);
        Assert.assertNotNull(tradesTable);
        assertThat(tradesContainer).hasChild(TRADES_TABLE_ID);

        verifyThat(tradesTable,hasNumRows(3));

        assertTradesTableColumnNames(tradesTable.getColumns());


    }

    private <T> void assertTradesTableColumnNames(ObservableList<TableColumn<T,
        String>> columns) {

        assertEquals(TradeViewField.TIMESTAMP.getName(), columns.get(0).getText());
        assertEquals(TradeViewField.PRICE.getName(), columns.get(1).getText());
        assertEquals(TradeViewField.AMOUNT.getName(), columns.get(2).getText());
        assertEquals(TradeViewField.VALUE.getName(), columns.get(3).getText());
    }

    private <T> void assertBookOrderColumnNames(ObservableList<TableColumn<T, String>> columns) {

        assertEquals(OrderViewField.PRICE.getName(), columns.get(0).getText());
        assertEquals(OrderViewField.AMOUNT.getName(), columns.get(1).getText());
        assertEquals(OrderViewField.VALUE.getName(), columns.get(2).getText());
        assertEquals(OrderViewField.SUM.getName(), columns.get(3).getText());
    }


}

