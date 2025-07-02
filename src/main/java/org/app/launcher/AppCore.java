package org.app.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.app.appcore.MainController;
import org.app.appcore.MainControllerImpl;
import org.app.config.ConfigManager;
import org.app.weathermode.view.LoadingScreen;

import java.util.Objects;

public class AppCore extends Application {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    @Override
    public void start(final Stage primaryStage) {
        final int minWidth = 1100;
        final int minHeight = 700;
        final Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png")));
        final MainController mainController = new MainControllerImpl();
        final Scene scene = new Scene(mainController.getRootView(), minWidth, minHeight);
        scene.getStylesheets().add(ClassLoader.getSystemResource("css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);
        primaryStage.setTitle("App Meteo & Viaggio");
        primaryStage.getIcons().add(icon);
        primaryStage.setOnCloseRequest(e -> ConfigManager.saveConfig(CONFIG_PATH));

        primaryStage.setOnShown(e -> {
            scene.getRoot().applyCss();
            scene.getRoot().layout();
            primaryStage.sizeToScene();
        });

        final LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.start(primaryStage, mainController.getAppController());
    }

    @Override
    public void stop() {
        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                for (final java.awt.Window w : java.awt.Window.getWindows()) {
                    w.dispose();
                }
            });
        } catch (final Exception ignored) {}
    }

}
