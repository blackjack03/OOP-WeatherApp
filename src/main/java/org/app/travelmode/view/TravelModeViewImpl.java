package org.app.travelmode.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    private Stage stage;
    private VBox vBox; //TODO: sistemare


    public TravelModeViewImpl(final TravelModeController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        this.stage = new Stage();
        this.stage.setTitle(STAGE_NAME);
        final BorderPane root = new BorderPane();

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

        final CityDateTimeInputBox departureInputBox = new CityDateTimeInputBox("Partenza", onDepartureCitySelected, fetcPredictions, onDateSelected, true);
        final CityInputBox arrivalInputBox = new CityInputBox("Arrivo", onArrivalCitySelected, fetcPredictions, true);


        final Button searchButton = new Button("CERCA PERCORSO");
        searchButton.setOnAction(event -> {
            final LocalTime departureTime = LocalTime.of(departureInputBox.getSelectedHour(), departureInputBox.getSelectedMinute());
            this.controller.setDepartureTime(departureTime);
            this.controller.startRouteAnalysis();

            //TODO: da sistemare

            // Ottieni l'immagine della mappa (ad esempio, Parigi)
            Image mapImage = this.controller.getStaticMap();

            // Crea un ImageView per mostrare l'immagine
            ImageView imageView = new ImageView(mapImage);
            imageView.setFitWidth(400);
            imageView.setFitHeight(400);
            imageView.setPreserveRatio(true);
            vBox.getChildren().add(imageView);
        });

        root.setLeft(departureInputBox);
        root.setRight(arrivalInputBox);

        //TODO: sistemare
        vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        final ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        root.setBottom(scrollPane);
        BorderPane.setAlignment(scrollPane, Pos.CENTER);


        root.setCenter(searchButton);
        final Scene scene = new Scene(root, 850, 600);
        this.stage.setScene(scene);
        this.stage.show();
    }

    @Override
    public void displayError(String message) {


    }
}
