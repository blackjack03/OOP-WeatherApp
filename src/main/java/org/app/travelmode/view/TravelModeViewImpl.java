package org.app.travelmode.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.app.travelmode.controller.TravelModeController;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TravelModeViewImpl implements TravelModeView {

    private static final String STAGE_NAME = "Navigation Mode Test";

    private final TravelModeController controller;

    private final Stage stage;
    private final Scene scene;
    private final BorderPane root;
    private VBox resultsVBox; //TODO: sistemare


    public TravelModeViewImpl(final TravelModeController controller) {
        this.controller = controller;
        this.stage = new Stage();
        this.stage.setTitle(STAGE_NAME);
        this.root = new BorderPane();
        this.scene = new Scene(root, 850, 600);
    }

    @Override
    public void start() {

        final BiConsumer<String, String> onDepartureCitySelected = (desc, pID) -> {
            this.controller.setDepartureLocation(desc);
            this.controller.setDeparturePlaceId(pID);
        };
        final BiConsumer<String, String> onArrivalCitySelected = (desc, pID) -> {
            this.controller.setArrivalLocation(desc);
            this.controller.setArrivalPlaceId(pID);
        };
        final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions = this.controller::getPlacePredictions;
        final Consumer<LocalDate> onDateSelected = this.controller::setDepartureDate;

        final CityDateTimeInputBoxImpl departureInputBox = new CityDateTimeInputBoxImpl("Partenza", onDepartureCitySelected, fetcPredictions, onDateSelected, true);
        final CityInputBoxImpl arrivalInputBox = new CityInputBoxImpl("Arrivo", onArrivalCitySelected, fetcPredictions, true);


        final Button searchButton = new Button("CERCA PERCORSO");
        searchButton.setOnAction(event -> {
            final LocalTime departureTime = LocalTime.of(departureInputBox.getSelectedHour(), departureInputBox.getSelectedMinute());
            this.controller.setDepartureTime(departureTime);
            searchButton.setDisable(true);
            this.controller.startRouteAnalysis();
        });

        final Button requestAlternatives = new Button("OTTIENI PERCORSI ALTERNATIVI");

        final StackPane centerPane = new StackPane();
        centerPane.getChildren().add(searchButton);
        centerPane.setStyle(
                "-fx-border-color: black;" +                // Colore del bordo
                        "-fx-border-width: 2px;" +          // Larghezza del bordo
                        "-fx-padding: 10px;" +               // Spazio interno
                        "-fx-background-color: white;" +    // Colore di sfondo
                        "-fx-border-radius: 15px; " +        // Arrotondamento del bordo
                        "-fx-background-radius: 15px;"      // Arrotondamento dello sfondo
        );
        centerPane.setMaxHeight(departureInputBox.getHeight());
        root.setCenter(centerPane);

        root.setLeft(departureInputBox);
        root.setRight(arrivalInputBox);

        //TODO: sistemare
        resultsVBox = new VBox(20);
        resultsVBox.setAlignment(Pos.CENTER);
        resultsVBox.setFillWidth(true);
        resultsVBox.setPrefHeight(scene.getHeight() * 0.7);
        final ScrollPane scrollPane = new ScrollPane(resultsVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        root.setBottom(scrollPane);
        BorderPane.setAlignment(scrollPane, Pos.CENTER);


        scene.heightProperty().addListener((obs, oldWidth, newWidth) -> {
            double availableHeight = scene.getHeight();
            resultsVBox.setPrefHeight(availableHeight * 0.7);
        });

        this.stage.setScene(scene);
        this.stage.show();
    }

    @Override
    public void displayError(String message) {


    }

    @Override
    public void displayResult(final String meteoScore, final String description, final String duration, final String arrivalTime, final Image mapImage) {
        Platform.runLater(() -> {
            final ResultBox resultBox = new ResultBox(meteoScore, description, duration, arrivalTime, mapImage, this.scene.getWindow());
            this.resultsVBox.getChildren().add(resultBox);
        });
    }
}
