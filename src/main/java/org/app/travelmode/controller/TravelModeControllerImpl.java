package org.app.travelmode.controller;

import javafx.application.Application;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.app.travelmode.model.PlaceAutocomplete;
import org.app.travelmode.model.PlaceAutocompleteImpl;
import org.app.travelmode.model.TravelRequestImpl;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.view.TravelModeView;
import org.app.travelmode.view.TravelModeViewImpl;

public class TravelModeControllerImpl extends Application implements TravelModeController {

    private final TravelModeView view = new TravelModeViewImpl(this);
    private TravelRequestImpl.Builder requestBuilder = new TravelRequestImpl.Builder();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.view.start();
    }

    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        PlaceAutocomplete placeAutocomplete = new PlaceAutocompleteImpl();
        return placeAutocomplete.getPlacePredictions(input);
    }

    @Override
    public TravelRequestImpl.Builder setDepartureLocation(final String departureLocation) {
        this.checkRequestBuilder();
        this.requestBuilder.setDepartureLocation(departureLocation);
        return this.requestBuilder;
    }

    @Override
    public TravelRequestImpl.Builder setDeparturePlaceId(final String departurePlaceId) {
        this.checkRequestBuilder();
        this.requestBuilder.setDeparturePlaceId(departurePlaceId);
        return this.requestBuilder;
    }

    @Override
    public TravelRequestImpl.Builder setArrivalLocation(final String arrivalLocation) {
        this.checkRequestBuilder();
        this.requestBuilder.setArrivalLocation(arrivalLocation);
        return this.requestBuilder;
    }

    @Override
    public TravelRequestImpl.Builder setArrivalPlaceId(final String arrivalPlaceId) {
        this.checkRequestBuilder();
        this.requestBuilder.setArrivalPlaceId(arrivalPlaceId);
        return this.requestBuilder;
    }

    @Override
    public TravelRequestImpl.Builder setDepartureTime(final LocalTime departureTime) {
        this.checkRequestBuilder();
        this.requestBuilder.setDepartureTime(departureTime);
        return this.requestBuilder;
    }

    @Override
    public TravelRequestImpl.Builder setDepartureDate(final LocalDate departureDate) {
        this.checkRequestBuilder();
        this.requestBuilder.setDepartureDate(departureDate);
        return this.requestBuilder;
    }

    /**
     * Check if the requestBuilder has already been used. If so, create a new one.
     */
    private void checkRequestBuilder() {
        if (this.requestBuilder.isConsumed()) {
            this.requestBuilder = new TravelRequestImpl.Builder();
        }
    }
}
