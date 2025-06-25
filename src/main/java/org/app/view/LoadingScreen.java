// LoadingScreen.java
package org.app.view;

import java.util.Optional;

import org.app.App;
import org.app.model.AppConfig;
import org.app.model.ConfigManager;
import org.app.model.LocationSelectorImpl;
import org.app.model.LookUp;
import org.app.model.IPLookUp;
import org.app.model.LocationSelector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadingScreen extends Application {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";

    private boolean showConfirmIP(final String message, final String title) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.initModality(Modality.APPLICATION_MODAL);  // blocca la finestra chiamante
        final Label content = new Label(message);
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 20px;");
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert.showAndWait()
                .orElse(ButtonType.NO) == ButtonType.YES;
    }

    public void showError(final String message, final String title) {
        final Runnable dialogTask = () -> {
            final Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(title == null || title.isBlank() ? "Errore" : title);
            alert.initModality(Modality.NONE);
            final Label content = new Label(message);
            content.setWrapText(true);
            content.setStyle("-fx-font-size: 20px;");
            alert.getDialogPane().setContent(content);
            final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            try {
                stage.getIcons().add(new Image(
                    getClass().getResourceAsStream("/error.png")));
            } catch (final Exception ignored) {}
            alert.show();
        };
        if (Platform.isFxApplicationThread()) {
            dialogTask.run();
        } else {
            Platform.runLater(dialogTask);
        }
    }

    @Override
    public void start(final Stage splashStage) {
        // UI di splash
        final ImageView imageView = new ImageView(
            new Image(getClass().getResourceAsStream("/logo.png"))
        );
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
        splashStage.show();

        // Task di background per LocationSelector
        final Task<LocationSelectorImpl> loadTask = new Task<>() {
            @Override
            protected LocationSelectorImpl call() throws Exception {
                return new LocationSelectorImpl();
            }
        };

        loadTask.setOnSucceeded(evt -> {
            final LocationSelector LS = loadTask.getValue();
            splashStage.close();
            Platform.runLater(() -> {
                try {
                    ConfigManager.loadConfig(CONFIG_PATH);
                    final AppConfig appConfig = ConfigManager.getConfig();
                    final App app = new App();
                    if (appConfig.getUserPreferences().getDefaultCity().isEmpty()) {
                        boolean useIP = false;
                        if (showConfirmIP("Vuoi usare la tua posizione (IP)?", "Usa posizione IP")) {
                            System.out.println("Using IP location");
                            useIP = true;
                            final LookUp lookUp = new IPLookUp();
                            if (!lookUp.lookup().isPresent()) {
                                this.showError("Impossibile determinare la posizione tramite IP!", "Errore di localizzazione");
                                useIP = false;
                            } else {
                                final Optional<Integer> cityID = LS.searchByLookUp(lookUp);
                                if (cityID.isPresent()) {
                                    System.out.println(lookUp);
                                    appConfig.getUserPreferences().setDefaultCity(cityID.get());
                                    ConfigManager.saveConfig(CONFIG_PATH);
                                } else {
                                    this.showError("Impossibile trovare la città corrispondente all'IP!", "Errore di localizzazione");
                                    useIP = false;
                                }
                            }
                        }
                        // this.showError("Errore di Prova", "Error Test");
                        boolean flagClose = false;
                        if (!useIP) {
                            final LocationSelectorGUI gui = new LocationSelectorGUI();
                            final Optional<Integer> result = gui.start(LS);
                            if (!result.isEmpty()) {
                                final int defaultCityID = result.get();
                                System.out.println(defaultCityID);
                                appConfig.getUserPreferences().setDefaultCity(defaultCityID);
                                ConfigManager.saveConfig(CONFIG_PATH);
                            } else {
                                flagClose = true;
                            }
                        }
                        if (!flagClose) {
                            app.setLocationSelector(LS);
                            final Stage mainStage = new Stage();
                            app.start(mainStage);
                        } else {
                            Platform.exit();
                        }
                    } else {
                        app.setLocationSelector(LS);
                        final Stage mainStage = new Stage();
                        app.start(mainStage);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            });
        });

        loadTask.setOnFailed(evt -> {
            loadTask.getException().printStackTrace();
            Platform.exit();
        });

        new Thread(loadTask, "loader-thread").start();
    }
}



/*package org.app.model;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoadingScreen extends Application {

    @Override
    public void start(Stage stage) {
        // Carica l’immagine (metti “logo.png” nelle risorse del progetto)
        ImageView imageView = new ImageView(
                new Image(getClass().getResourceAsStream("/logo.png"))
        );
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(150);   // riduci se serve

        Label loadingLabel = new Label("Loading...");
        loadingLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        VBox root = new VBox(10, imageView, loadingLabel);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 300, 200);

        stage.setScene(scene);
        stage.setTitle("Splash");
        stage.setResizable(false);    // opzionale
        stage.centerOnScreen();       // centra la finestra
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/
