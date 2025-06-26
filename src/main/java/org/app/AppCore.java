package org.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.app.appcore.MainController;
import org.app.appcore.MainControllerImpl;
import org.app.weathermode.controller.AppController;
import org.app.weathermode.view.LoadingScreen;

public class AppCore extends Application {

    @Override
    public void start(final Stage primaryStage) {
        final MainController mainController = new MainControllerImpl();
        final Scene scene = new Scene(mainController.getRootView(), 1000, 600);
        scene.getStylesheets().add(ClassLoader.getSystemResource("css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("App Meteo & Viaggio");

        final LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.start(primaryStage, mainController.getAppController());
    }

}
