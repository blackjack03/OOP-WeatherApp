package org.app.travelmode.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.app.travelmode.controller.TravelModeController;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TravelModeViewImpl implements TravelModeView {

    private static final String STAGE_NAME = "Navigation Mode Test";
    private static final String DEPARTURE_BOX_TITLE = "Partenza";
    private static final String ARRIVAL_BOX_TITLE = "Arrivo";
    private static final String SEARCH_BUTTON_TEXT = "CERCA PERCORSO";
    private static final String ALTERNATIVES_BUTTON_TEXT = "OTTIENI PERCORSI ALTERNATIVI";

    private final TravelModeController controller;
    //private final Stage stage;
    //private final Scene scene;
    private final VBox root;
    private VBox resultsVBox;


    public TravelModeViewImpl(final TravelModeController controller) {
        this.controller = controller;
        //this.stage = new Stage();
        //this.stage.setTitle(STAGE_NAME);
        this.root = new VBox(20);
        //this.scene = new Scene(root, 900, 650);
        //this.scene.getStylesheets().add(ClassLoader.getSystemResource("css/style.css").toExternalForm());
        final BiConsumer<String, String> onDepartureCitySelected = (desc, pID) -> {
            this.controller.setDepartureLocation(desc);
            this.controller.setDeparturePlaceId(pID);
        };
        final BiConsumer<String, String> onArrivalCitySelected = (desc, pID) -> {
            this.controller.setArrivalLocation(desc);
            this.controller.setArrivalPlaceId(pID);
        };
        final Function<String, List<PlaceAutocompletePrediction>> fetchPredictions = this.controller::getPlacePredictions;
        final Consumer<LocalDate> onDateSelected = this.controller::setDepartureDate;

        final CityDateTimeInputBoxImpl departureInputBox = new CityDateTimeInputBoxImpl(DEPARTURE_BOX_TITLE, onDepartureCitySelected, fetchPredictions, onDateSelected, true);
        final CityInputBoxImpl arrivalInputBox = new CityInputBoxImpl(ARRIVAL_BOX_TITLE, onArrivalCitySelected, fetchPredictions, true);


        final Button searchButton = new Button(SEARCH_BUTTON_TEXT);
        final Button requestAlternatives = new Button(ALTERNATIVES_BUTTON_TEXT);

        searchButton.setOnAction(event -> {
            this.controller.setDepartureTime(departureInputBox.getSelectedTime());
            this.resultsVBox.getChildren().clear();
            this.controller.startRouteAnalysis();
            requestAlternatives.setDisable(false);
        });
        searchButton.getStyleClass().add("main-button");

        requestAlternatives.setDisable(true);
        requestAlternatives.setOnAction(event -> {
            requestAlternatives.setDisable(true);
            this.controller.computeAlternativeResults();
        });
        requestAlternatives.getStyleClass().add("main-button");

        final VBox centerPane = new VBox(15);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.getChildren().addAll(searchButton, requestAlternatives);
        centerPane.setMaxHeight(departureInputBox.getHeight());

        final HBox topPane = new HBox(20);
        topPane.setAlignment(Pos.CENTER);
        topPane.setMaxWidth(1200);
        topPane.getChildren().addAll(departureInputBox, centerPane, arrivalInputBox);
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        topPane.getStyleClass().add("top-pane");

        resultsVBox = new VBox(20);
        resultsVBox.setAlignment(Pos.CENTER);
        resultsVBox.setFillWidth(true);
        resultsVBox.getStyleClass().add("results-section");

        final ScrollPane scrollPane = new ScrollPane(resultsVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("scroll-pane");

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(topPane, scrollPane);
        root.getStyleClass().add("root-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    @Override
    public void start() {

        //this.stage.setScene(scene);
        //this.stage.show();
    }

    @Override
    public void displayError(String message) {


    }

    @Override
    public void displayResult(int meteoScore, final String description, final String duration, final String arrivalDate, final String arrivalTime, final Image mapImage) {
        Platform.runLater(() -> {
            final Scene mainScene = this.controller.requestAppViewRootNode().getScene();
            final ResultBox resultBox = new ResultBox(meteoScore, description, duration, arrivalDate, arrivalTime, mapImage, mainScene.getWindow());
            this.resultsVBox.getChildren().add(resultBox);
        });
    }

    @Override
    public Parent getRootView() {
        return this.root;
    }
}
