package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsResponse;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.time.*;
import java.util.List;
import java.util.Optional;

public class TravelModeModelImpl implements TravelModeModel {

    private final TravelRequestImpl.Builder requestBuilder;
    private final GoogleApiClientFactory apiClientFactory;
    private final PlacePredictionsApiClient placePredictionsApiClient;
    private Directions directions;
    private Optional<DirectionsResponse> directionsResponse;


    public TravelModeModelImpl() {
        this.requestBuilder = new TravelRequestImpl.Builder();
        this.apiClientFactory = new GoogleApiClientFactoryImpl();
        this.placePredictionsApiClient = this.apiClientFactory.createPlacePredictionsApiClient();
        this.directionsResponse = Optional.empty();
    }

    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        return this.placePredictionsApiClient.getPlacePredictions(input);
    }

    @Override
    public void setDepartureLocation(final String departureLocation) {
        this.requestBuilder.setDepartureLocation(departureLocation);
    }

    @Override
    public void setDeparturePlaceId(final String departurePlaceId) {
        final PlaceDetailsApiClient placeDetailsApiClient = this.apiClientFactory.createPlaceDetailsApiClient();
        this.requestBuilder.setDeparturePlaceId(departurePlaceId)
                .setDepartureZoneId(placeDetailsApiClient.getTimezone(departurePlaceId));
    }

    @Override
    public void setArrivalLocation(final String arrivalLocation) {
        this.requestBuilder.setArrivalLocation(arrivalLocation);
    }

    @Override
    public void setArrivalPlaceId(final String arrivalPlaceId) {
        this.requestBuilder.setArrivalPlaceId(arrivalPlaceId);
    }

    @Override
    public void setDepartureTime(final LocalTime departureTime) {
        this.requestBuilder.setDepartureTime(departureTime);
    }

    @Override
    public void setDepartureDate(final LocalDate departureDate) {
        this.requestBuilder.setDepartureDate(departureDate);
    }

    @Override
    public void startDirectionsAnalysis(final TravelRequest travelRequest) {
        this.directions = new DirectionsImpl(travelRequest);
        directions.askForDirections();
    }

    @Override
    public TravelModeResult getTravelModeMainResult() {
        return this.directions.getMainResult();
    }

    @Override
    public List<TravelModeResult> getAlternativesResults() {
        return this.directions.getAlternativeResults();
    }

    @Override
    public TravelRequest finalizeTheRequest() {
        return this.requestBuilder.build();
    }
}
