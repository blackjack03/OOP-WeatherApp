package org.app.travelmode.view;

import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TravelModeViewImpl implements TravelModeView {

    private static final String STAGE_NAME = "Navigation Mode Test";
    private Stage stage;
    private TextField city1TextField;
    private TextField city2TextField;
    private ContextMenu suggestionsMenu;
    private final Label departureLabel = new Label("Partenza");
    private final Label arrivalLabel = new Label("Arrivo");


    public TravelModeViewImpl() {

    }

    @Override
    public void start() {
        this.stage = new Stage();
        this.stage.setTitle(STAGE_NAME);
        final BorderPane root = new BorderPane();
        final VBox city1VBox = new VBox();
        final VBox city2VBox = new VBox();
        this.suggestionsMenu = new ContextMenu();
        // Spinner per le ore (0-23)
        Spinner<Integer> hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));

        // Spinner per i minuti (0-59) con incremento di 5 minuti
        Spinner<Integer> minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5));
        // Aggiunta di elementi di menu (suggestioni fittizie)
        MenuItem suggestion1 = new MenuItem("Suggestion 1");
        MenuItem suggestion2 = new MenuItem("Suggestion 2");
        MenuItem suggestion3 = new MenuItem("Suggestion 3");

        // Aggiungi i MenuItem al ContextMenu
        this.suggestionsMenu.getItems().addAll(suggestion1, suggestion2, suggestion3);

        this.city1TextField = new TextField();
        this.city1TextField.setPromptText("Inserire la città...");
        this.city1TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 3) {
                suggestionsMenu.show(this.city1TextField, Side.BOTTOM, 0, 0);
            } else {
                suggestionsMenu.hide();
            }
        });

        this.city2TextField = new TextField();
        this.city2TextField.setPromptText("Inserire la città...");
        this.city2TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 3) {
                suggestionsMenu.show(this.city2TextField, Side.BOTTOM, 0, 0);
            } else {
                suggestionsMenu.hide();
            }
        });

        // Assegna un'azione a ciascun MenuItem
        suggestion1.setOnAction(e -> city1TextField.setText("Suggestion 1"));
        suggestion2.setOnAction(e -> city1TextField.setText("Suggestion 2"));
        suggestion3.setOnAction(e -> city1TextField.setText("Suggestion 3"));

        city1VBox.getChildren().addAll(this.departureLabel, this.city1TextField);
        city2VBox.getChildren().addAll(this.arrivalLabel, this.city2TextField);
        root.setLeft(city1VBox);
        root.setRight(city2VBox);
        final Scene scene = new Scene(root, 600, 400);
        this.stage.setScene(scene);
        this.stage.show();
    }

    @Override
    public void displayError(String message) {


    }

}
