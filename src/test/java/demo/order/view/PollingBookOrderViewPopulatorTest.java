package demo.order.view;

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import demo.order.domain.SyncHashMapOrderBook;
import javafx.beans.property.ListProperty;
import lombok.val;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import static demo.TestUtils.randPrice;
import static demo.order.parser.TestOrderUtils.*;
import static demo.order.service.websocket.DiffOrder.OrderType.BUY;
import static demo.order.service.websocket.DiffOrder.OrderType.SELL;
import static org.junit.Assert.assertEquals;

@RunWith(JfxRunner.class)
public class PollingBookOrderViewPopulatorTest {

    private PollingBookOrderViewPopulator updater;
    private ListProperty<OrderView.BidView> bidsProp;
    private ListProperty<OrderView.AskView> asksProp;

    @Test
    public void test_constructor(){

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingBookOrderViewPopulator(
                null,
                new SyncHashMapOrderBook()));

        Assertions.assertThrows(NullPointerException.class,
            () -> new PollingBookOrderViewPopulator(
                10,
                null));
    }

    @Test
    public void test_update_asks_bids_view_props_from_book() throws
        Exception {

        int seq=0;
        int maxOrders= 10;
        val price = randPrice();
        val lowPrice = price-1;
        val highPrice = price+1;
        //------------------GIVEN -----------------
        val orderBook = new SyncHashMapOrderBook();
        orderBook.update(batch(seq++, id("1"), SELL, price));
        orderBook.update(batch(seq++, id("2"), BUY, price));
        orderBook.update(batch(seq++, id("3"), BUY, highPrice));

        updater = new PollingBookOrderViewPopulator(maxOrders,orderBook);

        this.bidsProp = updater.getBidsProp();
        this.asksProp = updater.getAsksProp();

        //------------------WHEN -----------------
        updater.start();

        Thread.sleep(3000);
        //------------------THEN -----------------
        assertAskIdAt(id("1"), 0);
        assertBidIdAt(id("3"), 0);
        assertBidIdAt(id("2"), 1);

        //------------------GIVEN -----------------
        orderBook.update(batch(seq++, id("4"), SELL, highPrice));
        orderBook.update(batch(seq++, id("5"), SELL, lowPrice));
        orderBook.update(batchCancelled(seq++, id("2"), BUY));

        //------------------WHEN -----------------

        Thread.sleep(3000);

        //------------------THEN -----------------
        assertAskIdAt(id("5"), 0);
        assertAskIdAt(id("1"), 1);
        assertAskIdAt(id("4"), 2);

        assertEquals(1,bidsProp.size());
        assertBidIdAt( id("3"), 0);


    }

    private void assertBidIdAt(String id, int i) {
        assertEquals(id, bidsProp.get(i).getId());
    }

    private void assertAskIdAt(String id, int i) {
        assertEquals(id, asksProp.get(i).getId());
    }


}