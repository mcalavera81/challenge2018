package demo.app.frontend;

import demo.support.thread.ShutdownResourcesCleanUp;
import demo.app.backend.Backend;
import demo.app.config.AppConfiguration.FrontendConfig;
import demo.order.view.PollingOrderBookViewPopulator;
import demo.trade.view.PollingRecentTradesLogViewPopulator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


public class FrontendJavaFx implements Frontend {

    private FrontendConfig conf;

    private PollingOrderBookViewPopulator orderViewPopulator;
    private PollingRecentTradesLogViewPopulator tradeViewPopulator;

    public static Frontend of(
        FrontendConfig conf,
        Backend backend) {

        FrontendJavaFx fe = new FrontendJavaFx();
        fe.conf = conf;

        fe.orderViewPopulator =
            new PollingOrderBookViewPopulator(
                conf.getOrdersPollIntervalMs(),
                conf.getMaxOrders(),
                backend.getOrderBook());

        fe.tradeViewPopulator =
            new PollingRecentTradesLogViewPopulator(
                conf.getTradesPollIntervalMs(),
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