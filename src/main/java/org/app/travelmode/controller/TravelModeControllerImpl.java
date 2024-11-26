package org.app.travelmode.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import java.util.List;

import org.app.travelmode.model.PlaceAutocomplete;
import org.app.travelmode.model.PlaceAutocompleteImpl;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.view.TravelModeView;
import org.app.travelmode.view.TravelModeViewImpl;

public class TravelModeControllerImpl extends Application implements TravelModeController {

    private final TravelModeView view = new TravelModeViewImpl(this);

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.view.start();
    }

    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        PlaceAutocomplete placeAutocomplete = new PlaceAutocompleteImpl();
        return placeAutocomplete.getPlacePredictions(input);
    }
}
