package org.app.travelmode.model;

import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.util.List;

public interface PlaceAutocomplete {

    /**
     * Provides a list of cities or addresses based on the input text
     *
     * @param input String representing the characters written by the user
     * @return a list of predictions based on the input
     */
    List<PlaceAutocompletePrediction> getSuggestion(final String input);
}
