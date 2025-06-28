package org.app.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.app.appcore.MainController;
import org.app.appcore.MainControllerImpl;
import org.app.config.ConfigManager;
import org.app.weathermode.view.LoadingScreen;

public class AppCore extends Application {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    @Override
    public void start(final Stage primaryStage) {
        final MainController mainController = new MainControllerImpl();
        final Scene scene = new Scene(mainController.getRootView(), 1100, 700);
        scene.getStylesheets().add(ClassLoader.getSystemResource("css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.setTitle("App Meteo & Viaggio");
        primaryStage.setOnCloseRequest(e -> ConfigManager.saveConfig(CONFIG_PATH));

        primaryStage.setOnShown(e -> {
            scene.getRoot().applyCss();
            scene.getRoot().layout();
            primaryStage.sizeToScene();
        });

        final LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.start(primaryStage, mainController.getAppController());
    }

}
