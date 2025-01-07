package org.app.travelmode.controller;

import javafx.application.Application;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.app.travelmode.model.*;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.view.TravelModeView;
import org.app.travelmode.view.TravelModeViewImpl;

public class TravelModeControllerImpl extends Application implements TravelModeController {

    private final TravelModeView view = new TravelModeViewImpl(this);
    private final TravelModeModel model = new TravelModeModelImpl();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.view.start();
    }

    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        return this.model.getPlacePredictions(input);
    }

    @Override
    public TravelRequestImpl.Builder setDepartureLocation(final String departureLocation) {
        return this.model.setDepartureLocation(departureLocation);
    }

    @Override
    public TravelRequestImpl.Builder setDeparturePlaceId(final String departurePlaceId) {
        return this.model.setDeparturePlaceId(departurePlaceId);
    }

    @Override
    public TravelRequestImpl.Builder setArrivalLocation(final String arrivalLocation) {
        return this.model.setArrivalLocation(arrivalLocation);
    }

    @Override
    public TravelRequestImpl.Builder setArrivalPlaceId(final String arrivalPlaceId) {
        return this.model.setArrivalPlaceId(arrivalPlaceId);
    }

    @Override
    public TravelRequestImpl.Builder setDepartureTime(final LocalTime departureTime) {
        return this.model.setDepartureTime(departureTime);
    }

    @Override
    public TravelRequestImpl.Builder setDepartureDate(final LocalDate departureDate) {
        return this.model.setDepartureDate(departureDate);
    }

    @Override
    public void startRouteAnalysis() {
        this.model.startRouteAnalysis();
    }
}
