package demo.app.config;

import demo.app.config.AppConfiguration.FrontendConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.net.MalformedURLException;
import java.net.URL;

public class FrontendConfigTest {

    private static String DUMMY_STRING;
    private static URL DUMMY_URL;
    private static int DUMMY_INT;

    @BeforeClass
    public static void init() throws MalformedURLException {
        DUMMY_STRING = "";
        DUMMY_INT = 0;
        DUMMY_URL = new URL("http://example.com");
    }

    @Test
    public void test_FrontendConfig_builder_mainFxml_null() {


        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(null)
                .css(DUMMY_STRING)
                .maxOrders(DUMMY_INT)
                .maxTrades(DUMMY_INT)
                .tradesPollIntervalMs(DUMMY_INT)
                .build());
    }

    @Test
    public void test_FrontendConfig_builder_css_null() {

        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(DUMMY_URL)
                .css(null)
                .maxOrders(DUMMY_INT)
                .maxTrades(DUMMY_INT)
                .tradesPollIntervalMs(DUMMY_INT)
                .build());
    }

    @Test
    public void test_FrontendConfig_builder_max_orders_null() {

        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(DUMMY_URL)
                .css(DUMMY_STRING)
                .maxOrders(null)
                .maxTrades(DUMMY_INT)
                .tradesPollIntervalMs(DUMMY_INT)
                .build());
    }

    @Test
    public void test_FrontendConfig_builder_max_trades_null() {


        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(DUMMY_URL)
                .css(DUMMY_STRING)
                .maxOrders(DUMMY_INT)
                .maxTrades(null)
                .tradesPollIntervalMs(DUMMY_INT)
                .build());
    }

    @Test
    public void test_FrontendConfig_builder_trades_polling_interval_null() {


        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(DUMMY_URL)
                .css(DUMMY_STRING)
                .maxOrders(DUMMY_INT)
                .maxTrades(DUMMY_INT)
                .tradesPollIntervalMs(null)
                .build());


    }

}
