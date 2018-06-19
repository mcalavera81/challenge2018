package demo.shared.config;

import demo.shared.config.AppConfiguration.BackendConfig;
import demo.shared.config.AppConfiguration.FrontendConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.net.MalformedURLException;
import java.net.URL;

public class AppConfigurationTest {


    private static String DUMMY_STRING;
    private static URL DUMMY_URL;
    private static int DUMMY_INT;

    @BeforeClass
    public static void init() throws MalformedURLException {
        DUMMY_STRING ="";
        DUMMY_INT=0;
        DUMMY_URL = new URL("http://example.com");
    }

    @Test
    public void test_FrontendConfig_builder(){


        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(null)
                .css(DUMMY_STRING)
                .maxOrders(DUMMY_INT)
                .maxTrades(DUMMY_INT)
                .build());


        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(DUMMY_URL)
                .css(null)
                .maxOrders(DUMMY_INT)
                .maxTrades(DUMMY_INT)
                .build());

        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(DUMMY_URL)
                .css(DUMMY_STRING)
                .maxOrders(null)
                .maxTrades(DUMMY_INT)
                .build());


        Assertions.assertThrows(NullPointerException.class,
            () -> FrontendConfig.builder()
                .mainFxml(DUMMY_URL)
                .css(DUMMY_STRING)
                .maxOrders(DUMMY_INT)
                .maxTrades(null)
                .build());





    }

    @Test
    public void test_BackendConfig_builder(){

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(null)
                .bitsoWebsocketUri(DUMMY_STRING)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(null)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .orderStreamBuffer(null)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(null)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(null)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)

                .build());

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(null)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(null)
                .build());


    }

    @Test
    public void test_load_non_existent_file(){

        Assertions.assertThrows(RuntimeException.class,
            ()->AppConfiguration.tryBuild("not_a_file"));

    }

    @Test
    public void test_load_non_existent_property(){

        Assertions.assertThrows(RuntimeException.class,
            ()->AppConfiguration.tryBuild("demo_test_missing_prop.conf"));

    }

    @Test
    public void test_load_property_wrong_type(){

        Assertions.assertThrows(RuntimeException.class,
            ()->AppConfiguration.tryBuild("demo_test_wrong_type.conf"));

    }
}