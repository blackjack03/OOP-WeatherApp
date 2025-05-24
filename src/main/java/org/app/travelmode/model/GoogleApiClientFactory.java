package org.app.travelmode.model;

public interface GoogleApiClientFactory {

    DirectionApiClient createDirectionApiClient();

    PlaceDetailsApiClient createPlaceDetailsApiClient();

    PlacePredictionsApiClient createPlacePredictionsApiClient();

    StaticMapApiClient createStaticMapApiClient();
}
