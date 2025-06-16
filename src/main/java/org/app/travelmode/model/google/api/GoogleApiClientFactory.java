package org.app.travelmode.model.google.api;

import org.app.travelmode.model.google.impl.DirectionApiClientImpl;
import org.app.travelmode.model.google.impl.PlaceDetailsApiClient;
import org.app.travelmode.model.google.impl.PlacePredictionsApiClient;

/**
 * Factory interface for creating various Google API clients.
 * This factory provides methods to instantiate different types of Google API clients
 * while encapsulating the complexity of their creation and configuration.
 */
public interface GoogleApiClientFactory {

    /**
     * Creates a new instance of DirectionApiClientImpl configured to interact
     * with Google Directions API.
     *
     * @return a new {@link DirectionApiClientImpl} instance
     */
    DirectionApiClientImpl createDirectionApiClient();

    /**
     * Creates a new instance of PlaceDetailsApiClient configured to interact
     * with Google Place Details API.
     *
     * @return a new {@link PlaceDetailsApiClient} instance
     */
    PlaceDetailsApiClient createPlaceDetailsApiClient();

    /**
     * Creates a new instance of PlacePredictionsApiClient configured to interact
     * with Google Place Autocomplete API.
     *
     * @return a new {@link PlacePredictionsApiClient} instance
     */
    PlacePredictionsApiClient createPlacePredictionsApiClient();

    /**
     * Creates a new instance of StaticMapApiClient configured to interact
     * with Google Static Maps API.
     *
     * @return a new {@link StaticMapApiClient} instance
     */
    StaticMapApiClient createStaticMapApiClient();
}
