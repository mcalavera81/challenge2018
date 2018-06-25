package demo.order.source.poller;

import demo.app.Constants;
import demo.order.source.poller.dto.OrderData;
import demo.order.source.poller.parser.OrderDataParser.OrderDataParserException;
import io.vavr.control.Try;
import org.json.JSONObject;
import org.junit.Test;

import static demo.TestHelpers.loadJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class OrderDataParserTest {

    @Test
    public void test_build_OrderData_empty(){
        JSONObject jsonObject = new JSONObject("{}");
        Try<OrderData> build = OrderData.build(jsonObject);
        assertThat(build.isSuccess(), is(false));
        assertThat(build.getCause(), is(instanceOf(OrderDataParserException.class)));

    }


    @Test
    public void test_build_OrderData_missing_amount(){
        String filename = "order/order_data_missing_amount.json";
        Try<JSONObject> jsonObject = loadJson(filename);
        Try<OrderData> build = OrderData.build(jsonObject.get());
        assertTrue(build.isEmpty());
        assertThat(build.getCause(), is(instanceOf(OrderDataParserException.class)));
        assertThat(build.getCause().toString(), containsString("JSONObject[\"amount\"] not found"));


    }

    @Test
    public void test_build_OrderData_missing_price(){
        String filename = "order/order_data_missing_price.json";
        Try<JSONObject> jsonObject = loadJson(filename);
        Try<OrderData> build = OrderData.build(jsonObject.get());
        assertTrue(build.isEmpty());
        assertThat(build.getCause(), is(instanceOf(OrderDataParserException.class)));
        assertThat(build.getCause().toString(), containsString
            ("JSONObject[\"price\"] not found"));

    }

    @Test
    public void test_build_OrderData_missing_id(){
        String filename = "order/order_data_missing_id.json";
        Try<JSONObject> jsonObject = loadJson(filename);
        Try<OrderData> build = OrderData.build(jsonObject.get());
        assertTrue(build.isEmpty());
        assertThat(build.getCause(), is(instanceOf(OrderDataParserException.class)));
        assertThat(build.getCause().toString(), containsString
            ("JSONObject[\"oid\"] not found"));

    }


    @Test
    public void test_build_OrderData_ok(){
        Try<JSONObject> jsonObject = loadJson("order/order_data_ok.json");
        Try<OrderData> build = OrderData.build(jsonObject.get());
        assertTrue(build.isSuccess());
    }

    private JSONObject OrderData(String price, String amount, String oid){
        return new JSONObject().
            put("book", Constants.BitsoBook.BTC_MXN.id()).
            put("price",price).
            put("amount",amount).
            put("oid",oid);
    }

}
