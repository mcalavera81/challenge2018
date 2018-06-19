package demo;

import demo.shared.config.AppConfiguration;
import demo.shared.config.AppConfiguration.FrontendConfig;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class Main extends Application{


    @Override
    public void start(Stage primaryStage){

        AppConfiguration conf = AppConfiguration.tryBuild();

        Backend backend = InMemoryBackend.build(conf.getBackendConfig());
        backend.start();
        buildSceneAndSetupStage(
            conf.getFrontendConfig(),
            primaryStage,
            backend);

    }

    private void buildSceneAndSetupStage(FrontendConfig conf,
                                         Stage primaryStage,
                                         Backend backend) {


        val frontend = FrontendJavaFx.of(conf, backend);

        frontend.start();

        primaryStage.setScene(frontend.buildScene());
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }


    public static void main(String[] args) {


        launch(args);
    }


}
