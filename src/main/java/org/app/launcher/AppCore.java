package org.app.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.app.appcore.MainController;
import org.app.appcore.MainControllerImpl;
import org.app.config.ConfigManager;
import org.app.weathermode.view.LoadingScreen;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Objects;

/**
 * Applicazione principale che avvia la GUI, imposta l’icona,
 * lo stile e gestisce il caricamento iniziale e la chiusura.
 */
public class AppCore extends Application {

    private static final String CONFIG_PATH = "app_config/configuration.json";

    /**
     * Punto di ingresso JavaFX: costruisce la scena principale,
     * applica gli stylesheet, imposta icona, dimensioni minime,
     * titolo e mostra la schermata di caricamento.
     *
     * @param primaryStage lo {@link Stage} primario fornito da JavaFX
     */
    @Override
    public void start(final Stage primaryStage) {
        final int minWidth = 1100;
        final int minHeight = 700;
        final Image icon = new Image(Objects.requireNonNull(
            AppCore.class.getResourceAsStream("/logo.png")
        ));
        final MainController mainController = new MainControllerImpl();
        final Scene scene = new Scene(mainController.getRootView(), minWidth, minHeight);
        scene.getStylesheets().add(
            ClassLoader.getSystemResource("css/style.css").toExternalForm()
        );

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

    /**
     * Pulizia delle risorse AWT/Swing alla chiusura dell’applicazione.
     * Vengono chiuse tutte le finestre AWT aperte.
     */
    @Override
    public void stop() {
        // CHECKSTYLE: EmptyCatchBlock OFF
        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                for (final java.awt.Window w : java.awt.Window.getWindows()) {
                    w.dispose();
                }
            });
        } catch (final Exception ignored) { } // NOPMD
        // CHECKSTYLE: EmptyCatchBlock ON
    }

}
