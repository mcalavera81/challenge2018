package demo.shared.config;

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


    private static AppConfiguration build(String configFile){

        Config c = ConfigFactory.load(configFile);

        val fc = FrontendConfig.builder()
            .mainFxml(getURL(c, "view.fxml.main"))
            .css(getString(c, "view.css"))
            .maxOrders(getInt(c, "view.maxOrders"))
            .maxTrades(getInt(c, "view.maxTrades"))
            .build();

        val bc = BackendConfig.builder()
            .bitsoRestUri(getString(c, "backend.provider.bitso.rest.uri"))
            .bitsoWebsocketUri(getString(c, "backend.provider.bitso.websocket.uri"))
            .tradesRestDefaultLimit(getInt(c, "backend.trade.rest.default.limit"))
            .tradesMemoryCacheSize(getInt(c, "backend.trade.memory.cache.size"))
            .tradingAlgorithmUpTicks(getInt(c, "backend.trade.algorithm.upTicks"))
            .tradingAlgorithmDownTicks(getInt(c, "backend.trade.algorithm.downTicks"))
            .orderStreamBuffer(getInt(c, "backend.order.stream.buffer"))
            .build();

        return new AppConfiguration(fc,bc);

    }

    @Builder
    @Getter
    public static class FrontendConfig {
        @NonNull
        private final URL mainFxml;
        @NonNull
        private final String css;
        @NonNull
        private Integer maxOrders;
        @NonNull
        private Integer maxTrades;
    }

    @Builder
    @Getter
    public static class BackendConfig {

        @NonNull
        private final String bitsoRestUri;
        @NonNull
        private final String bitsoWebsocketUri;
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

    public static AppConfiguration tryBuild(String... configFile) {

        String file = configFile!=null && configFile.length==1?
            configFile[0]:
            defaultConfigFile;

        try {
            return build(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
