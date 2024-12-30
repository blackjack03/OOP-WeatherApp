package org.app.travelmode.controller;

import javafx.application.Application;
import javafx.scene.image.Image;
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
    public void setDepartureLocation(final String departureLocation) {
        this.model.setDepartureLocation(departureLocation);
    }

    @Override
    public void setDeparturePlaceId(final String departurePlaceId) {
        this.model.setDeparturePlaceId(departurePlaceId);
    }

    @Override
    public void setArrivalLocation(final String arrivalLocation) {
        this.model.setArrivalLocation(arrivalLocation);
    }

    @Override
    public void setArrivalPlaceId(final String arrivalPlaceId) {
        this.model.setArrivalPlaceId(arrivalPlaceId);
    }

    @Override
    public void setDepartureTime(final LocalTime departureTime) {
        this.model.setDepartureTime(departureTime);
    }

    @Override
    public void setDepartureDate(final LocalDate departureDate) {
        this.model.setDepartureDate(departureDate);
    }

    @Override
    public void startRouteAnalysis() {
        this.model.startRouteAnalysis();
    }

    public Image getStaticMap() {
        return this.model.getStaticMap();
    }
}
