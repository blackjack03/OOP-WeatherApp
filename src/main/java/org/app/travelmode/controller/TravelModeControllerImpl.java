package org.app.travelmode.controller;

import javafx.application.Application;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.app.travelmode.model.core.TravelModeModel;
import org.app.travelmode.model.core.TravelModeModelImpl;
import org.app.travelmode.model.exception.MapGenerationException;
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
public class TravelModeControllerImpl extends Application implements TravelModeController {

    private final TravelModeView view = new TravelModeViewImpl(this);
    private final TravelModeModel model = new TravelModeModelImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final Stage primaryStage) {
        this.view.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        return this.model.getPlacePredictions(input);
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
        this.model.setDeparturePlaceId(departurePlaceId);
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
    public void startRouteAnalysis() {
        final TravelRequest travelRequest = this.model.finalizeTheRequest();
        this.model.startDirectionsAnalysis(travelRequest);
        final TravelModeResult mainResult = this.model.getTravelModeMainResult();
        displayResult(mainResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeAlternativeResults() {
        final Optional<List<TravelModeResult>> travelModeResults = this.model.getAlternativesResults();
        travelModeResults.ifPresentOrElse(results -> results.forEach(this::displayResult),
                () -> this.showWarningOnGUI("No alternatives", "Non sono presenti percorsi alternativi")); //TODO: migliorare gestione
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showErrorOnGUI(final String title, final String message) {  //TODO: migliorare gestione
        System.out.println(title + ": " + message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWarningOnGUI(final String title, final String message) {    //TODO: migliorare gestione
        System.out.println(title + ": " + message);
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
            this.showErrorOnGUI("Errore nel calcolo del punteggio meteo", e.getMessage()); //TODO: migliorare gestione
        } catch (final MapGenerationException e) {
            this.showErrorOnGUI("Errore nella creazione della mappa", e.getMessage()); //TODO: migliorare gestione
        }
    }
}
