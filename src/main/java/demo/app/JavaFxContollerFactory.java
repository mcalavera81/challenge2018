package demo.app;

import demo.order.view.OrderBookController;
import demo.order.view.OrderViewPopulator;
import demo.shared.config.AppConfiguration.FrontendConfig;
import demo.trade.view.RecentTradesController;
import demo.trade.view.TradeViewPopulator;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static demo.shared.parser.UtilParser.getStackTrace;

@Slf4j
class JavaFxContollerFactory implements Callback<Class<?>, Object> {

    private final FrontendConfig conf;
    private final OrderViewPopulator orderPopulator;
    private final TradeViewPopulator tradePopulator;

    public JavaFxContollerFactory(FrontendConfig conf,
                                  OrderViewPopulator orderPopulator,
                                  TradeViewPopulator tradePopulator) {

        this.conf = conf;
        this.orderPopulator = orderPopulator;
        this.tradePopulator = tradePopulator;
    }

    @Override
    public Object call(Class<?> clazz) {
        try {
            return initController(clazz);
        } catch (Exception e) {
            log.error(
                "Error instantiating the controller: {}", getStackTrace(e)
            );
            throw new RuntimeException(e);
        }
    }

    private Object initController(Class<?> clazz) throws Exception{

        Object controllerInstance = null;
        if (OrderBookController.class.isAssignableFrom(clazz)) {

            val orderBookControllerConstructor =
                OrderBookController.class.getDeclaredConstructor(OrderViewPopulator.class);

            controllerInstance = orderBookControllerConstructor
                .newInstance(orderPopulator);

        } else if (RecentTradesController.class.isAssignableFrom(clazz)) {


            val recentTradesControllerConstructor =
                RecentTradesController.class.getDeclaredConstructor(TradeViewPopulator.class);

            controllerInstance = recentTradesControllerConstructor
                .newInstance(tradePopulator);
        }

        return controllerInstance;
    }
}
