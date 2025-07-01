package org.app.travelmode.controller;

import javafx.scene.Parent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.app.appcore.MainController;
import org.app.travelmode.model.core.TravelModeModel;
import org.app.travelmode.model.core.TravelModeModelImpl;
import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.exception.MapGenerationException;
import org.app.travelmode.model.exception.TravelRequestException;
import org.app.travelmode.model.exception.WeatherDataException;
import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.api.TravelRequest;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.view.TravelModeView;
import org.app.travelmode.view.TravelModeViewImpl;

/**
 * Implementation of the TravelMode controller that manages the interaction between
 * the view and model components of the application. This class handles user inputs,
 * processes travel requests, and updates the view with results.
 */
public class TravelModeControllerImpl implements TravelModeController {

    private final TravelModeView view;
    private final TravelModeModel model;
    private final MainController mainController;

    /**
     * Creates a new instance of the TravelModeControllerImpl.
     *
     * <p>Initializes the controller with the necessary components:
     * <ul>
     *     <li>Creates a new view instance</li>
     *     <li>Initializes the travel mode model</li>
     *     <li>Stores the reference to the main controller</li>
     * </ul>
     *
     * @param mainController the main application controller that manages the overall application state
     */
    public TravelModeControllerImpl(final MainController mainController) {
        this.view = new TravelModeViewImpl(this);
        this.model = new TravelModeModelImpl();
        this.mainController = mainController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startTravelMode() {
        this.model.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        try {
            return this.model.getPlacePredictions(input);
        } catch (final IOException e) {
            this.showErrorOnGUI("Errore nell'autocompletamento", e.getMessage() + "\nRitenta l'inserimento");
        }
        return List.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDepartureLocation(final String departureLocation) {
        this.model.setDepartureLocation(departureLocation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeparturePlaceId(final String departurePlaceId) {
        try {
            this.model.setDeparturePlaceId(departurePlaceId);
        } catch (final IOException e) {
            this.showErrorOnGUI("Errore sui dati del luogo di partenza",
                    "Impossibile ottenere il fuso orario del luogo di partenza\n" + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArrivalLocation(final String arrivalLocation) {
        this.model.setArrivalLocation(arrivalLocation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArrivalPlaceId(final String arrivalPlaceId) {
        this.model.setArrivalPlaceId(arrivalPlaceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDepartureTime(final LocalTime departureTime) {
        this.model.setDepartureTime(departureTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDepartureDate(final LocalDate departureDate) {
        this.model.setDepartureDate(departureDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean startRouteAnalysis() {
        try {
            final TravelRequest travelRequest = this.model.finalizeTheRequest();
            this.model.startDirectionsAnalysis(travelRequest);
            final TravelModeResult mainResult = this.model.getTravelModeMainResult();
            displayResult(mainResult);
        } catch (final TravelRequestException e) {
            this.showWarningOnGUI("Richiesta incompleta", e.getMessage());
            return false;
        } catch (final DirectionsApiException e) {
            this.showErrorOnGUI("Errore nella richiesta dei percorsi", e.getMessage());
            return false;
        } catch (final WeatherDataException e) {
            this.showErrorOnGUI("Errore nella richiesta dei dati meteo", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeAlternativeResults() {
        try {
            final Optional<List<TravelModeResult>> travelModeResults = this.model.getAlternativesResults();
            travelModeResults.ifPresentOrElse(results -> results.forEach(this::displayResult),
                    () -> this.showWarningOnGUI("No alternatives", "Non sono presenti percorsi alternativi"));
        } catch (final WeatherDataException e) {
            this.showErrorOnGUI("Errore nella richiesta dei dati meteo", e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showErrorOnGUI(final String title, final String message) {
        this.mainController.showErrorOnGUI(title, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWarningOnGUI(final String title, final String message) {
        this.mainController.showWarningOnGUI(title, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parent gatTraveleModeView() {
        return this.view.getRootView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parent requestAppViewRootNode() {
        return this.mainController.getRootView();
    }

    /**
     * Displays a single travel mode result in the view.
     *
     * @param travelModeResult the result to be displayed containing travel information
     *                         such as duration, arrival time, meteo score, and route summary
     */
    private void displayResult(final TravelModeResult travelModeResult) {
        try {
            final String durationString = travelModeResult.getDurationString();
            final LocalDateTime arrivalDateTime = travelModeResult.getArrivalTime();
            final String arrivalTime = arrivalDateTime.toLocalTime().getHour() + ":" + arrivalDateTime.toLocalTime().getMinute();
            final String arrivalDate = arrivalDateTime.toLocalDate().toString();
            this.view.displayResult(travelModeResult.getMeteoScore(), travelModeResult.getSummary(), durationString,
                    arrivalDate, arrivalTime, travelModeResult.getMapImage());
        } catch (final IllegalStateException e) {
            this.showErrorOnGUI("Errore nel calcolo del punteggio meteo", e.getMessage());
        } catch (final MapGenerationException e) {
            this.showErrorOnGUI("Errore nella creazione della mappa", e.getMessage());
        }
    }
}
