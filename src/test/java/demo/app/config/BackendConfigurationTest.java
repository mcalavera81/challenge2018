package demo.app.config;

import demo.app.config.AppConfiguration.BackendConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class BackendConfigurationTest {


    private static String DUMMY_STRING;
    private static int DUMMY_INT;

    @BeforeClass
    public static void init() {
        DUMMY_STRING = "";
        DUMMY_INT = 0;
    }


    @Test
    public void test_BackendConfig_builder_rest_uri_null() {

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(null)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());
    }

    @Test
    public void test_BackendConfig_builder_websocket_uri_null() {

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(null)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());
    }

    @Test
    public void test_BackendConfig_builder_limiter_permits_null() {

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(null)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());
    }


    @Test
    public void test_BackendConfig_builder_limiter_period_null() {

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(null)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());
    }

    @Test
    public void test_BackendConfig_builder_order_buffer_null() {
        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(null)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());
    }

    @Test
    public void test_BackendConfig_builder_rest_default_limit_null() {
        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(null)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());

    }

    @Test
    public void test_BackendConfig_builder_trades_cache_size_null() {
        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(null)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(DUMMY_INT)

                .build());
    }

    @Test
    public void
    test_BackendConfig_builder_trading_algorihtm_upticks_null() {

        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(null)
                .tradingAlgorithmDownTicks(DUMMY_INT)
                .build());
    }

    @Test
    public void test_BackendConfig_builder() {
        Assertions.assertThrows(NullPointerException.class,
            () -> BackendConfig.builder()
                .bitsoRestUri(DUMMY_STRING)
                .bitsoWebsocketUri(DUMMY_STRING)
                .bitsoRestLimiterPermits(DUMMY_INT)
                .bitsoRestLimiterPeriod(DUMMY_INT)
                .orderStreamBuffer(DUMMY_INT)
                .tradesRestDefaultLimit(DUMMY_INT)
                .tradesMemoryCacheSize(DUMMY_INT)
                .tradingAlgorithmUpTicks(DUMMY_INT)
                .tradingAlgorithmDownTicks(null)
                .build());


    }


}