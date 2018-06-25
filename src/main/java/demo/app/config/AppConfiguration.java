package demo.app.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.*;

import java.net.URL;


@Getter
@AllArgsConstructor
public class AppConfiguration {

    private static final String defaultConfigFile = "demo.conf";


    private final FrontendConfig frontendConfig;
    private final BackendConfig backendConfig;

    private static AppConfiguration instance;

    public static synchronized AppConfiguration getInstance() {
        if (instance == null) {
            instance = build(defaultConfigFile);
        }
        return instance;
    }
    private static AppConfiguration build(String configFile) {
        Config c = ConfigFactory.load(configFile);

        val fc = FrontendConfig.builder()
            .mainFxml(getURL(c, "view.fxml.main"))
            .css(getString(c, "view.css"))
            .maxOrders(getInt(c, "view.order.table.size"))
            .maxTrades(getInt(c, "view.trade.table.size"))
            .tradesPollIntervalMs(getInt(c, "view.trade.poll.interval.ms"))
            .ordersPollIntervalMs(getInt(c, "view.order.poll.interval.ms"))
            .majorFormat(getString(c, "view.formatting.major"))
            .minorFormat(getString(c, "view.formatting.minor"))
            .build();

        val bc = BackendConfig.builder()
            .bitsoRestUri(getString(c, "backend.provider.bitso.rest.uri"))
            .bitsoWebsocketUri(getString(c, "backend.provider.bitso.websocket.uri"))
            .bitsoRestLimiterPermits(getInt(c,"backend.provider.bitso.rest.limiter.permits"))
            .bitsoRestLimiterPeriod(getInt(c,"backend.provider.bitso.rest.limiter.period.sec"))
            .tradesRestDefaultLimit(getInt(c, "backend.trade.rest.default.limit"))
            .tradesMemoryCacheSize(getInt(c, "backend.trade.memory.cache.size"))
            .tradingAlgorithmUpTicks(getInt(c, "backend.trade.algorithm.upTicks"))
            .tradingAlgorithmDownTicks(getInt(c, "backend.trade.algorithm.downTicks"))
            .orderStreamBuffer(getInt(c, "backend.order.stream.buffer"))
            .build();

        return new AppConfiguration(fc, bc);

    }

    @Builder
    @Getter
    public static class FrontendConfig {
        @NonNull
        private final URL mainFxml;
        @NonNull
        private final String css;
        @NonNull
        private final Integer maxOrders;
        @NonNull
        private final Integer maxTrades;
        @NonNull
        private final Integer tradesPollIntervalMs;
        @NonNull
        private final Integer ordersPollIntervalMs;
        @NonNull
        private final String majorFormat;
        @NonNull
        private final String minorFormat;


    }

    @Builder
    @Getter
    public static class BackendConfig {

        @NonNull
        private final String bitsoRestUri;
        @NonNull
        private final String bitsoWebsocketUri;
        @NonNull
        private final Integer bitsoRestLimiterPermits;
        @NonNull
        private final Integer bitsoRestLimiterPeriod;
        @NonNull
        private final Integer tradesRestDefaultLimit;
        @NonNull
        private final Integer orderStreamBuffer;
        @NonNull
        private final Integer tradesMemoryCacheSize;
        @NonNull
        private final Integer tradingAlgorithmUpTicks;
        @NonNull
        private final Integer tradingAlgorithmDownTicks;


    }

    private static int getInt(Config conf, String path) {
        return conf.getInt(path);
    }

    private static String getString(Config conf, String path) {
        return conf.getString(path);
    }

    private static URL getURL(Config conf, String path) {
        return conf.getClass().getResource(conf.getString(path));
    }


}
