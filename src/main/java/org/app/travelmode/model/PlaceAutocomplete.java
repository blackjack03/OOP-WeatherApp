package org.app.travelmode.model;

import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.util.List;

public interface PlaceAutocomplete {

    /**
     * Provides a list of {@link PlaceAutocompletePrediction} based on the input text
     *
     * @param input String representing the characters written by the user
     * @return a list of predictions based on the input
     */
    List<PlaceAutocompletePrediction> getPlacePredictions(final String input);
}
