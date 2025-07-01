package org.app.weathermode.view;

import java.util.Optional;

import org.app.weathermode.controller.Controller;
import org.app.config.AppConfig;
import org.app.config.ConfigManager;
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
 * <p>Finestra Splash di avvio che svolge tre funzioni principali:</p>
 * <ol>
 *   <li>Mostrare un logo e messaggio di caricamento all’utente mentre vengono
 *       inizializzate le risorse pesanti (<em>LocationSelector</em>).</li>
 *   <li>Eseguire il bootstrap della configurazione: se l’utente non ha ancora
 *       impostato una città di default chiede se usare la posizione IP o
 *       mostra una finestra di selezione manuale.</li>
 *   <li>Lanciare l’<strong>AppController</strong> e aprire la finestra
 *       principale solo dopo che tutte le dipendenze sono pronte.</li>
 * </ol>
 * <p>La classe è <em>stand‑alone</em> rispetto a JavaFX <code>Application</code>:
 * viene istanziata e richiamata dal metodo <code>main</code> dell’applicazione.</p>
 */
public class LoadingScreen {

    /** Percorso del file di configurazione utente. */
    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    private final Stage splashStage;

    /**
     * Costruisce la finestra splash con logo e label “Loading…”. Nessuna logica
     * di business viene eseguita in questo costruttore.
     */
    public LoadingScreen() {
        final double imageFitWidth = 150.0;
        final double rootSpacing = 10.0;
        final double sceneWidth = 325.0;
        final double sceneHeight = 225.0;
        final double fontSize = 20.0;
        final String imagePath = "/logo.png";
        final String loadingText = "Loading...";
        final String windowTitle = "Weather App - Loading...";

        // Initialize stage
        splashStage = new Stage();

        // Static UI elements
        final ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(imageFitWidth);

        final Label loadingLabel = new Label(loadingText);
        loadingLabel.setFont(Font.font("System", FontWeight.BOLD, fontSize));

        final VBox root = new VBox(rootSpacing, imageView, loadingLabel);
        root.setAlignment(Pos.CENTER);

        // Configure and show scene
        splashStage.setScene(new Scene(root, sceneWidth, sceneHeight));
        splashStage.setTitle(windowTitle);
        splashStage.setResizable(false);
        splashStage.centerOnScreen();
    }

    /* ===================== dialog helper ====================== */

    /**
     * Mostra un <em>confirmation dialog</em> bloccante per chiedere se usare la
     * posizione IP.
     * @return <code>true</code> se l’utente seleziona “Yes”.
     */
    private boolean showConfirmIP(final String message, final String title) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.initModality(Modality.APPLICATION_MODAL);
        final Label content = new Label(message); content.setWrapText(true);
        content.setStyle("-fx-font-size: 20px;");
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    /* ========================= avvio ========================== */

    /**
     * Avvia il task di caricamento e gestisce il flusso di selezione città.
     * @param primaryStage stage principale passato dall’<code>Application</code>.
     * @param appController controller MVC da inizializzare una volta pronta la vista.
     */
    public void start(final Stage primaryStage, final Controller appController) {
        splashStage.show();

        /* ---- Task di background per caricare il CSV delle città ---- */
        final Task<LocationSelectorImpl> loadTask = new Task<>() {
            @Override protected LocationSelectorImpl call() { return new LocationSelectorImpl(); }
        };

        /* ---- Successo ---------------------------------------------------- */
        loadTask.setOnSucceeded(evt -> {
            final LocationSelector LS = loadTask.getValue();
            splashStage.close();
            Platform.runLater(() -> handlePostLoad(primaryStage, appController, LS));
        });

        /* ---- Fallimento -------------------------------------------------- */
        loadTask.setOnFailed(evt -> {
            loadTask.getException().printStackTrace();
            Platform.exit();
        });

        new Thread(loadTask, "loader-thread").start();
    }

    /* ===================== flusso post-load ==================== */
    private void handlePostLoad(final Stage primaryStage, final Controller appController, final LocationSelector LS) {
        try {
            final AppConfig appConfig = ConfigManager.getConfig();
            if (appConfig.getUserPreferences().getDefaultCity().isEmpty()) {
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
                            CustomErrorGUI.showErrorJFX("Impossibile trovare la città corrispondente all'IP!", "Errore di localizzazione");
                        }
                    } else {
                        CustomErrorGUI.showErrorJFX("Impossibile determinare la posizione tramite IP!", "Errore di localizzazione");
                    }
                }
                boolean closeApp = false;
                if (!useIP) {
                    final LocationSelectorGUI gui = new LocationSelectorGUI();
                    final Optional<Integer> result = gui.start(LS);
                    if (result.isPresent()) {
                        appConfig.getUserPreferences().setDefaultCity(result.get());
                        ConfigManager.saveConfig(CONFIG_PATH);
                    } else {
                        closeApp = true;
                    }
                }
                if (closeApp) {
                    Platform.exit();
                    return;
                }
            }
            appController.getApp().setLocationSelector(LS);
            appController.start();
            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

}
