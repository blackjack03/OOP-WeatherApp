package org.app.weathermode.view;

import org.app.weathermode.controller.AppController;
import org.app.weathermode.model.AppConfig;
import org.app.weathermode.model.ConfigManager;
import org.app.weathermode.model.MoonPhases;
import org.app.weathermode.model.MoonPhasesImpl;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Optional;

/**
 * Finestra modale con le impostazioni dell’app.
 */
public class SettingsWindow extends Stage {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";
    private final AppController controller;

    public SettingsWindow(final AppController controller) {
        this.controller = controller;

        setTitle("Impostazioni");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);

        /* ---------- pulsanti ---------- */
        /* final Button travelModeBtn = new Button("Apri modalità Travel Mode");
        travelModeBtn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        travelModeBtn.setMaxWidth(Double.MAX_VALUE);
        travelModeBtn.setOnAction(e -> openTravelMode()); */

        final Button moonBtn = new Button("Visualizza Luna di Oggi");
        moonBtn.setStyle("-fx-font-size: 18px;");
        moonBtn.setMaxWidth(Double.MAX_VALUE);
        moonBtn.setOnAction(e -> openMoon());

        final Button changeCityBtn = new Button("Cambia Città");
        changeCityBtn.setStyle("-fx-font-size: 18px;");
        changeCityBtn.setMaxWidth(Double.MAX_VALUE);
        changeCityBtn.setOnAction(e -> openChangeCity());

        /* ---------- layout ---------- */
        final VBox root = new VBox(15, moonBtn, changeCityBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        final Scene scene = new Scene(root);
        setScene(scene);

        // Imposta dimensione minima
        setMinWidth(400);
        setMinHeight(300);

        // Imposta dimensione iniziale = dimensione minima
        setWidth(400);
        setHeight(300);

        setResizable(false);
    }

    /* ---------- handler ---------- */

    /** Lancia il frame Swing che mostra le fasi lunari di oggi. */
    private void openMoon() {
        final Thread t = new Thread(() -> {
            final MoonPhases moon = new MoonPhasesImpl();
            final Optional<Map<String, String>> moonInfo = moon.getMoonInfo();
            if (moonInfo.isEmpty()) {
                System.err.println("Errore nel recupero delle informazioni lunari.");
                CustomErrorGUI.showError(
                    "Errore nel recupero delle informazioni lunari.",
                    "Errore!"
                );
                return;
            }
            ImageFromURLSwing.viewIMG(
                moon.getImageURL(moonInfo.get().get("image_name")),
                moonInfo.get().get("state"),
                "Today MOON Info"
            );
        }, "MoonInfoSwing");
        t.setDaemon(true);
        t.start();
    }

    /** Lancia il frame Swing per cambiare città. */
    private void openChangeCity() {
        final Thread t = new Thread(() -> {
            final LocationSelectorGUI gui = new LocationSelectorGUI();
            final Optional<Integer> res = gui.start(App.getLocationSelector());
            res.ifPresent(id ->
                Platform.runLater(() -> {
                        final AppConfig appConfig = ConfigManager.getConfig();
                        appConfig.getUserPreferences().setDefaultCity(id);
                        ConfigManager.saveConfig(CONFIG_PATH);
                        System.out.println("City ID = " + id);
                        this.controller.forceRefresh();
                    }
                )
            );
        }, "ChangeCitySwing");
        t.setDaemon(true);
        t.start();
    }

}
