package org.app.travelmode.model.google.dto.placeautocomplete;

import java.util.List;

/**
 * {@code PlaceAutocompleteResponse} represents the response from a place autocomplete API.
 * <p>
 * It contains a list of {@link PlaceAutocompletePrediction} suggestions and a status string
 * indicating the outcome of the API request (e.g., "OK", "ZERO_RESULTS", "OVER_QUERY_LIMIT").
 * </p>
 *
 * <p>This class is typically used as a data model when parsing JSON responses
 * from location-based autocomplete services.</p>
 */
public class PlaceAutocompleteResponse {

    private List<PlaceAutocompletePrediction> predictions;
    private String status;

    /**
     * Constructs an empty {@code PlaceAutocompleteResponse}.
     */
    public PlaceAutocompleteResponse() {

    }

    /**
     * Returns the list of autocomplete predictions.
     *
     * @return A list of {@link PlaceAutocompletePrediction} objects
     */
    public List<PlaceAutocompletePrediction> getPredictions() {
        return this.predictions;
    }

    /**
     * Returns the status of the autocomplete response.
     *
     * @return A string representing the response status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Returns a string representation of this response, including predictions and status.
     *
     * @return A string describing the response content
     */
    @Override
    public String toString() {
        return "PlaceAutocompleteResponse [predictions=\n" + predictions + ",\n status=" + status + "]";
    }
}
