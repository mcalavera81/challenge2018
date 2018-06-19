package demo;

import demo.order.view.PollingBookOrderViewPopulator;
import demo.shared.config.AppConfiguration.FrontendConfig;
import demo.shared.service.ThreadRunner;
import demo.trade.view.PollingRestTradeViewPopulator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


public class FrontendJavaFx implements Frontend {

    private FrontendConfig conf;

    private PollingBookOrderViewPopulator orderViewPopulator;
    private PollingRestTradeViewPopulator tradeViewPopulator;

    public ThreadRunner getOrderViewPopulator() {
        return orderViewPopulator;
    }

    public ThreadRunner getTradeViewPopulator() {
        return tradeViewPopulator;
    }

    public static Frontend of(
        FrontendConfig conf,
        Backend backend) {

        FrontendJavaFx fe = new FrontendJavaFx();
        fe.conf = conf;

        fe.orderViewPopulator =
            new PollingBookOrderViewPopulator(
                conf.getMaxOrders(),
                backend.getOrderBook());

        fe.tradeViewPopulator =
            new PollingRestTradeViewPopulator(
                conf.getMaxTrades(),
                backend.getRecentTradesLog());

        return fe;

    }

    @Override
    public void start() {
        this.orderViewPopulator.start();
        this.tradeViewPopulator.start();

        Runtime.getRuntime().addShutdownHook(
            new ShutdownResourcesCleanUp(
                this.orderViewPopulator,
                this.tradeViewPopulator));
    }

    public Scene buildScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(conf.getMainFxml());

            loader.setControllerFactory(
                new JavaFxContollerFactory(
                    conf,
                    this.orderViewPopulator,
                    this.tradeViewPopulator)
            );

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(conf.getCss());
            return scene;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}