package demo.app;

import demo.shared.config.AppConfiguration;
import javafx.application.Application;
import javafx.scene.Scene;
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

        val frontend = FrontendJavaFx.of(conf.getFrontendConfig(), backend);
        frontend.start();

        setupStage(primaryStage, frontend.buildScene());

    }


    private void setupStage(Stage primaryStage, Scene scene) {

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }

    public static void main(String[] args) {


        launch(args);
    }


}
