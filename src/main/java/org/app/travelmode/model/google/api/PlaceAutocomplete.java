package org.app.travelmode.model.google.api;

import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.io.IOException;
import java.util.List;

/**
 * Defines a contract for place autocomplete functionality that suggests locations
 * based on user input.
 *
 * <p>This interface supports:
 * <ul>
 *     <li>Real-time place predictions</li>
 *     <li>Location-based suggestions</li>
 *     <li>Partial text matching</li>
 *     <li>Geographic place resolution</li>
 * </ul>
 */
public interface PlaceAutocomplete {

    /**
     * Provides a list of {@link PlaceAutocompletePrediction} based on the input text.
     *
     * @param input a {@link String} representing the input the text to use for place prediction (can be partial).
     * @return an immutable list of place predictions,
     * or an empty list if no predictions are found or if the response is invalid.
     * @throws IOException if there's an error communicating with the Google Places API
     *                     or parsing the response
     */
    List<PlaceAutocompletePrediction> getPlacePredictions(String input) throws IOException;
}
