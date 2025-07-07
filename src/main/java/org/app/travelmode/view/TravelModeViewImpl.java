package org.app.travelmode.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.app.travelmode.controller.TravelModeController;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of the travel mode view that handles the user interface for travel route planning.
 *
 * <p>This class provides a graphical interface that:
 * <ul>
 *     <li>Allows users to input departure and arrival locations</li>
 *     <li>Enables date and time selection for travel</li>
 *     <li>Displays route search results with weather information</li>
 *     <li>Supports alternative route discovery</li>
 * </ul>
 */
public class TravelModeViewImpl implements TravelModeView {

    private static final String DEPARTURE_BOX_TITLE = "Partenza";
    private static final String ARRIVAL_BOX_TITLE = "Arrivo";
    private static final String SEARCH_BUTTON_TEXT = "CERCA PERCORSO";
    private static final String ALTERNATIVES_BUTTON_TEXT = "OTTIENI PERCORSI ALTERNATIVI";
    private static final int ROOT_SPACING = 20;
    private static final int TOP_PANE_SPACING = 20;
    private static final int CENTER_PANE_SPACING = 15;
    private static final int TOP_PANE_MAX_WIDTH = 1200;
    private static final int RESULT_BOX_SPACING = 20;

    private final TravelModeController controller;
    private final VBox root;
    private final VBox resultsVBox;

    /**
     * Constructs a new travel mode view with the specified controller.
     *
     * <p>This constructor:
     * <ul>
     *     <li>Initializes the UI components</li>
     *     <li>Sets up input handlers for departure and arrival locations</li>
     *     <li>Configures date/time selection functionality</li>
     *     <li>Creates and arranges the search interface</li>
     *     <li>Sets up the results display area</li>
     * </ul>
     *
     * @param controller the controller that handles user interactions and data processing
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Reference to TravelModeController is intentionally shared so the view can invoke "
                    + "business logic; compliant with MVC."
    )
    public TravelModeViewImpl(final TravelModeController controller) {
        this.controller = controller;
        this.root = new VBox(ROOT_SPACING);

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

        final CityDateTimeInputBoxImpl departureInputBox = CityDateTimeInputBoxImpl.create(DEPARTURE_BOX_TITLE,
                onDepartureCitySelected, fetchPredictions, onDateSelected, true);
        final CityInputBoxImpl arrivalInputBox = CityInputBoxImpl.create(ARRIVAL_BOX_TITLE,
                onArrivalCitySelected, fetchPredictions, true);

        resultsVBox = new VBox(RESULT_BOX_SPACING);
        resultsVBox.setAlignment(Pos.CENTER);
        resultsVBox.setFillWidth(true);
        resultsVBox.getStyleClass().add("results-section");

        final Button searchButton = new Button(SEARCH_BUTTON_TEXT);
        final Button requestAlternatives = new Button(ALTERNATIVES_BUTTON_TEXT);

        searchButton.setOnAction(event -> {
            requestAlternatives.setDisable(true);
            if (departureInputBox.isDateTimePersonalizationClosed()) {
                this.controller.setDepartureDate(LocalDate.now());
            }
            this.controller.setDepartureTime(departureInputBox.getSelectedTime());
            this.resultsVBox.getChildren().clear();
            if (this.controller.startRouteAnalysis()) {
                requestAlternatives.setDisable(false);
            }
        });
        searchButton.getStyleClass().add("main-button");

        requestAlternatives.setDisable(true);
        requestAlternatives.setOnAction(event -> {
            requestAlternatives.setDisable(true);
            this.controller.computeAlternativeResults();
        });
        requestAlternatives.getStyleClass().add("main-button");

        final VBox centerPane = new VBox(CENTER_PANE_SPACING);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.getChildren().addAll(searchButton, requestAlternatives);
        centerPane.setMaxHeight(departureInputBox.getHeight());

        final HBox topPane = new HBox(TOP_PANE_SPACING);
        topPane.setAlignment(Pos.CENTER);
        topPane.setMaxWidth(TOP_PANE_MAX_WIDTH);
        topPane.getChildren().addAll(departureInputBox, centerPane, arrivalInputBox);
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        topPane.getStyleClass().add("top-pane");

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayResult(final int meteoScore, final String description, final String duration,
                              final String arrivalDate, final String arrivalTime, final Image mapImage) {
        Platform.runLater(() -> {
            final Scene mainScene = this.controller.requestAppViewRootNode().getScene();
            final ResultBox resultBox = ResultBox.create(meteoScore, description, duration,
                    arrivalDate, arrivalTime, mapImage, mainScene.getWindow());
            this.resultsVBox.getChildren().add(resultBox);
        });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "Exposing the root view is intentional: external modules "
                    + "need full access for styling and composition."
    )
    @Override
    public Parent getRootView() {
        return this.root;
    }
}
