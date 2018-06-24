package demo.order.view;

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import demo.order.business.state.SyncHashMapOrderBook;
import demo.order.source.stream.dto.DiffOrder.OrderType;
import javafx.beans.property.ListProperty;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import static demo.TestUtils.randPrice;
import static demo.order.source.TestParserOrderUtils.*;
import static demo.order.source.stream.dto.DiffOrder.OrderType.BUY;
import static demo.order.source.stream.dto.DiffOrder.OrderType.SELL;
import static org.junit.Assert.assertEquals;

@RunWith(JfxRunner.class)
public class PollingOrderBookViewPopulatorTest {

    private PollingOrderBookViewPopulator viewPopulator;
    private ListProperty<OrderTableRow> bidsProp;
    private ListProperty<OrderTableRow> asksProp;

    private long  sequence;
    private SyncHashMapOrderBook book;

    @Before
    public void init() {
        book = new SyncHashMapOrderBook();
        sequence =0;
    }

    @Test
    public void test_constructor(){
        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingOrderBookViewPopulator(
                null,
                10,
                new SyncHashMapOrderBook()));
        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingOrderBookViewPopulator(
                1000,
                null,
                new SyncHashMapOrderBook()));

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingOrderBookViewPopulator(
                1000,
                10,
                null));
    }

    @Test
    public void test_update_asks_bids_view_props_from_book() throws
        Exception {

        //------------------GIVEN -----------------
        int maxOrders= 10;
        int pollingIntervalMs = 1000;
        val price = randPrice();
        val lowPrice = price-1;
        val highPrice = price+1;
        newBookOrder(id("1"), SELL, price);
        newBookOrder(id("2"), BUY, price);
        newBookOrder(id("3"), BUY, highPrice);


        viewPopulator = new PollingOrderBookViewPopulator(
            pollingIntervalMs,
            maxOrders,
            book);

        this.bidsProp = viewPopulator.getBidsProp();
        this.asksProp = viewPopulator.getAsksProp();

        //------------------WHEN -----------------
        viewPopulator.start();

        Thread.sleep(2000);
        //------------------THEN -----------------
        assertAskIds("1");
        assertBidIds("3,2".split(","));
        //------------------GIVEN -----------------
        newBookOrder(id("4"), SELL, highPrice);
        newBookOrder(id("5"), SELL, lowPrice);
        cancelOrder(BUY,id("2"));

        //------------------WHEN -----------------

        Thread.sleep(2000);

        //------------------THEN -----------------
        assertAskIds("5,1,4".split(","));

        assertEquals(1,bidsProp.size());
        assertBidIds("3");

        viewPopulator.stop();

    }

    private void cancelOrder(OrderType type,String id) {
        book.update(batchCancelled(this.sequence++, id, type));
    }


    public void newBookOrder(String orderId, OrderType type, double price){
        book.update(batch(this.sequence++, orderId, type, price));
    }

    private void assertAskIds(String... ids) {
        for (int i = 0; i < ids.length; i++) {
            assertEquals(ids[i], asksProp.get(i).getId());
        }
    }

    private void assertBidIds(String... ids) {
        for (int i = 0; i < ids.length; i++) {
            assertEquals(ids[i], bidsProp.get(i).getId());
        }
    }


}