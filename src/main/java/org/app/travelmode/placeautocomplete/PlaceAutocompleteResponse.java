package org.app.travelmode.placeautocomplete;

import java.util.List;

/**
 * This class represents the response of a call to the Google Place Autocomplete API.
 */
public class PlaceAutocompleteResponse {

    private List<PlaceAutocompletePrediction> predictions;
    private String status;

    public PlaceAutocompleteResponse() {

    }

    /**
     * Returns a list of possible predictions
     *
     * @return a list of possible predictions
     */
    public List<PlaceAutocompletePrediction> getPredictions() {
        return this.predictions;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "PlaceAutocompleteResponse [predictions=\n" + predictions + ",\n status=" + status + "]";
    }
}
