package org.app.travelmode.view;

import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.app.travelmode.controller.TravelModeController;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.util.List;

public class TravelModeViewImpl implements TravelModeView {

    private static final String STAGE_NAME = "Navigation Mode Test";

    private final TravelModeController controller;

    private Stage stage;
    private TextField city1TextField;
    private TextField city2TextField;
    private ContextMenu departureSuggestionsMenu;
    private ContextMenu arrivalSuggestionsMenu;
    private final Label departureLabel = new Label("Partenza");
    private final Label arrivalLabel = new Label("Arrivo");


    public TravelModeViewImpl(final TravelModeController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        this.stage = new Stage();
        this.stage.setTitle(STAGE_NAME);
        final BorderPane root = new BorderPane();
        final VBox city1VBox = new VBox();

        this.departureSuggestionsMenu = new ContextMenu();
        this.arrivalSuggestionsMenu = new ContextMenu();

        this.city1TextField = new TextField();
        this.city1TextField.setMinWidth(200);
        this.city1TextField.setPromptText("Inserire la città o l'indirizzo");
        city1TextField.setOnKeyTyped(event -> {
            if (!event.getCharacter().equals("\r") && !event.getCharacter().equals("\t")) {
                final String actualText = this.city1TextField.getText();
                if (actualText.length() >= 3) {
                    final List<PlaceAutocompletePrediction> placePredictions = controller.getPlacePredictions(actualText);
                    this.updateSuggestionsMenu(departureSuggestionsMenu, city1TextField, placePredictions);
                    if (!departureSuggestionsMenu.isShowing()) {
                        departureSuggestionsMenu.show(this.city1TextField, Side.BOTTOM, 0, 0);
                    }
                } else {
                    departureSuggestionsMenu.hide();
                }
            }
        });

        final HBox departureHBox = new HBox();
        // Spinner per le ore (0-23)
        Spinner<Integer> hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));

        // Spinner per i minuti (0-59) con incremento di 5 minuti
        Spinner<Integer> minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5));

        final DatePicker datePicker = new DatePicker();
        final LocalDate oggi = LocalDate.now();
        final LocalDate start = oggi;
        final LocalDate end = oggi.plusDays(6);
        datePicker.setDayCellFactory((dP) -> new DateCell() {
            @Override
            public void updateItem(final LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(start) || date.isAfter(end)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f4f4f4; -fx-text-fill: #b0b0b0; -fx-opacity: 0.6;");
                } else {
                    setStyle("-fx-background-color: #ffffff; -fx-text-fill: #2c3e50;");
                    setOnMouseEntered(e -> setStyle("-fx-background-color: #d1e8ff; -fx-text-fill: #2c3e50;"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: #ffffff; -fx-text-fill: #2c3e50;"));
                }
            }
        });
        datePicker.setShowWeekNumbers(false);
        datePicker.setEditable(false);


        final VBox city2VBox = new VBox();
        this.city2TextField = new TextField();
        this.city2TextField.setMinWidth(200);
        this.city2TextField.setPromptText("Inserire la città o l'indirizzo");
        city2TextField.setOnKeyTyped(event -> {
            if (!event.getCharacter().equals("\r") && !event.getCharacter().equals("\t")) {
                final String actualText = this.city2TextField.getText();
                if (actualText.length() >= 3) {
                    final List<PlaceAutocompletePrediction> placePredictions = controller.getPlacePredictions(actualText);
                    this.updateSuggestionsMenu(arrivalSuggestionsMenu, city2TextField, placePredictions);
                    if (!arrivalSuggestionsMenu.isShowing()) {
                        arrivalSuggestionsMenu.show(this.city2TextField, Side.BOTTOM, 0, 0);
                    }
                } else {
                    arrivalSuggestionsMenu.hide();
                }
            }
        });

        departureHBox.getChildren().addAll(hourSpinner, minuteSpinner);
        city1VBox.getChildren().addAll(this.departureLabel, this.city1TextField, departureHBox, datePicker);
        city1VBox.setStyle(
                "-fx-border-color: black;" +                // Colore del bordo
                        "-fx-border-width: 2px;" +          // Larghezza del bordo
                        "-fx-padding: 10px;" +               // Spazio interno
                        "-fx-background-color: white;" +    // Colore di sfondo
                        "-fx-border-radius: 15px; " +        // Arrotondamento del bordo
                        "-fx-background-radius: 15px;"      // Arrotondamento dello sfondo
        );
        city1VBox.setMaxSize(220, departureLabel.getHeight() + this.city1TextField.getHeight() + departureHBox.getHeight() + datePicker.getHeight());
        city2VBox.setStyle(
                "-fx-border-color: black;" +                // Colore del bordo
                        "-fx-border-width: 2px;" +          // Larghezza del bordo
                        "-fx-padding: 10px;" +               // Spazio interno
                        "-fx-background-color: white;" +    // Colore di sfondo
                        "-fx-border-radius: 15px; " +        // Arrotondamento del bordo
                        "-fx-background-radius: 15px;"      // Arrotondamento dello sfondo
        );
        city2VBox.setMaxSize(220, this.arrivalLabel.getHeight() + this.city2TextField.getHeight());
        city2VBox.getChildren().addAll(this.arrivalLabel, this.city2TextField);
        root.setLeft(city1VBox);
        root.setRight(city2VBox);
        final Scene scene = new Scene(root, 850, 600);
        this.stage.setScene(scene);
        this.stage.show();
    }

    @Override
    public void displayError(String message) {


    }

    /**
     * Update a {@link ContextMenu} with the new {@link PlaceAutocompletePrediction}
     *
     * @param menu        The menu to update
     * @param anchor      The {@link TextField} to which the {@code menu} is anchored
     * @param predictions The list of new predictions
     */
    private void updateSuggestionsMenu(final ContextMenu menu, final TextField anchor, final List<PlaceAutocompletePrediction> predictions) {
        menu.getItems().clear();
        for (final PlaceAutocompletePrediction prediction : predictions) {
            final MenuItem menuItem = new MenuItem();
            menuItem.setText(prediction.getDescription());
            menuItem.setOnAction(event -> {
                anchor.setText(prediction.getDescription());
                menu.hide();
            });
            menu.getItems().add(menuItem);
        }
    }
}
