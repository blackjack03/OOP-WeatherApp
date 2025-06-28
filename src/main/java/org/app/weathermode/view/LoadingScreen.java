package org.app.weathermode.view;

import java.util.Optional;

import org.app.config.AppConfig;
import org.app.config.ConfigManager;
import org.app.weathermode.controller.Controller;
import org.app.weathermode.model.IPLookUp;
import org.app.weathermode.model.LocationSelector;
import org.app.weathermode.model.LocationSelectorImpl;
import org.app.weathermode.model.LookUp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * <h2>LoadingScreen</h2>
 * Splash screen di avvio: carica le risorse, gestisce la scelta della città
 * (IP o selezione manuale Swing) e lancia il controller principale.
 */
public class LoadingScreen {

    /** Percorso del file di configurazione utente. */
    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    private final Stage splashStage;

    /** Costruisce la finestra splash. */
    public LoadingScreen() {
        splashStage = new Stage();

        /* --- UI statica ------------------------------------------------- */
        final ImageView imageView = new ImageView(new Image(
                getClass().getResourceAsStream("/logo.png")));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(150);

        final Label loadingLabel = new Label("Loading...");
        loadingLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        final VBox root = new VBox(10, imageView, loadingLabel);
        root.setAlignment(Pos.CENTER);

        splashStage.setScene(new Scene(root, 325, 225));
        splashStage.setTitle("Weather App - Loading...");
        splashStage.setResizable(false);
        splashStage.centerOnScreen();
    }

    /* ================= helper dialog ================= */

    /** Confirmation dialog bloccante. */
    private boolean showConfirmIP(final String message, final String title) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.initModality(Modality.APPLICATION_MODAL);
        final Label content = new Label(message);
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 20px;");
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    /* ==================== avvio ======================= */

    /**
     * Avvia il task di caricamento e gestisce il flusso di selezione città.
     *
     * @param primaryStage  lo stage principale fornito dall'Application
     * @param appController controller MVC principale
     */
    public void start(final Stage primaryStage, final Controller appController) {
        splashStage.show();

        /* Task di background per caricare il CSV delle città */
        final Task<LocationSelectorImpl> loadTask = new Task<>() {
            @Override
            protected LocationSelectorImpl call() {
                return new LocationSelectorImpl();
            }
        };

        /* Successo */
        loadTask.setOnSucceeded(evt -> {
            final LocationSelector LS = loadTask.getValue();
            splashStage.close();
            Platform.runLater(() -> handlePostLoad(primaryStage, appController, LS));
        });

        /* Fallimento */
        loadTask.setOnFailed(evt -> {
            loadTask.getException().printStackTrace();
            Platform.exit();
        });

        new Thread(loadTask, "loader-thread").start();
    }

    /* ================ flusso post-load ================ */

    private void handlePostLoad(final Stage primaryStage,
                                final Controller appController,
                                final LocationSelector LS) {
        try {
            final AppConfig appConfig = ConfigManager.getConfig();

            /* --- se non esiste ancora una città di default --- */
            if (appConfig.getUserPreferences().getDefaultCity().isEmpty()) {

                /* 1. tentativo posizione IP */
                boolean useIP = false;
                if (showConfirmIP("Vuoi usare la tua posizione (IP)?", "Usa posizione IP")) {
                    final LookUp lookUp = new IPLookUp();
                    if (lookUp.lookup().isPresent()) {
                        final Optional<Integer> cityID = LS.searchByLookUp(lookUp);
                        if (cityID.isPresent()) {
                            appConfig.getUserPreferences().setDefaultCity(cityID.get());
                            ConfigManager.saveConfig(CONFIG_PATH);
                            useIP = true;
                        } else {
                            CustomErrorGUI.showErrorJFX(
                                    "Impossibile trovare la città corrispondente all'IP!",
                                    "Errore di localizzazione");
                        }
                    } else {
                        CustomErrorGUI.showErrorJFX(
                                "Impossibile determinare la posizione tramite IP!",
                                "Errore di localizzazione");
                    }
                }

                /* 2. finestra Swing se IP fallisce / rifiutato */
                if (!useIP) {

                    /* Apri la GUI Swing in un thread dedicato
                     * (così l'FX Application Thread non resta in attesa) */
                    Thread citySelectThread = new Thread(() -> {
                        Optional<Integer> result =
                                new LocationSelectorGUI().start(LS);

                        /* torna sul thread JavaFX */
                        Platform.runLater(() -> {
                            if (result.isPresent()) {
                                appConfig.getUserPreferences()
                                         .setDefaultCity(result.get());
                                ConfigManager.saveConfig(CONFIG_PATH);

                                continueStartup(primaryStage, appController, LS);
                            } else {
                                Platform.exit();
                            }
                        });
                    }, "city-select");
                    citySelectThread.setDaemon(true);
                    citySelectThread.start();

                    /* uscire subito: il resto verrà eseguito dal Platform.runLater */
                    return;
                }
            }

            /* --- città già impostata (o identificata da IP) --- */
            continueStartup(primaryStage, appController, LS);

        } catch (final Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    /** Prosegue l’avvio una volta nota la città di default. */
    private void continueStartup(final Stage primaryStage,
                                 final Controller appController,
                                 final LocationSelector LS) {
        appController.getApp().setLocationSelector(LS);
        appController.start();
        primaryStage.show();
    }
}
