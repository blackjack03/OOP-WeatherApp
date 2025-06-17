package org.app.travelmode.model.core;

import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.google.api.GoogleApiClientFactory;
import org.app.travelmode.model.google.impl.GoogleApiClientFactoryImpl;
import org.app.travelmode.model.google.impl.PlaceDetailsApiClient;
import org.app.travelmode.model.google.impl.PlacePredictionsApiClient;
import org.app.travelmode.model.routing.api.Directions;
import org.app.travelmode.model.routing.impl.DirectionsImpl;
import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.api.TravelRequest;
import org.app.travelmode.model.travel.impl.TravelRequestImpl;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.time.*;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the TravelModeModel interface that handles trip planning and route analysis functionality
 * to check weather conditions that may be encountered during the trip.
 */
public class TravelModeModelImpl implements TravelModeModel {

    private final TravelRequestImpl.Builder requestBuilder;
    private final GoogleApiClientFactory apiClientFactory;
    private final PlacePredictionsApiClient placePredictionsApiClient;
    private Directions directions;
    private Optional<DirectionsResponse> directionsResponse;

    /**
     * Constructs a new TravelModeModelImpl instance.
     * Initializes all necessary components including API clients and request builder.
     */
    public TravelModeModelImpl() {
        this.requestBuilder = new TravelRequestImpl.Builder();
        this.apiClientFactory = new GoogleApiClientFactoryImpl();
        this.placePredictionsApiClient = this.apiClientFactory.createPlacePredictionsApiClient();
        this.directionsResponse = Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        return this.placePredictionsApiClient.getPlacePredictions(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDepartureLocation(final String departureLocation) {
        this.requestBuilder.setDepartureLocation(departureLocation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeparturePlaceId(final String departurePlaceId) {
        final PlaceDetailsApiClient placeDetailsApiClient = this.apiClientFactory.createPlaceDetailsApiClient();
        this.requestBuilder.setDeparturePlaceId(departurePlaceId)
                .setDepartureZoneId(placeDetailsApiClient.getTimezone(departurePlaceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArrivalLocation(final String arrivalLocation) {
        this.requestBuilder.setArrivalLocation(arrivalLocation);
    }

    @Override
    public void setArrivalPlaceId(final String arrivalPlaceId) {
        this.requestBuilder.setArrivalPlaceId(arrivalPlaceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDepartureTime(final LocalTime departureTime) {
        this.requestBuilder.setDepartureTime(departureTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDepartureDate(final LocalDate departureDate) {
        this.requestBuilder.setDepartureDate(departureDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startDirectionsAnalysis(final TravelRequest travelRequest) {
        this.directions = new DirectionsImpl(travelRequest);
        try {
            directions.askForDirections();
        } catch (Exception e) {
            System.out.println("Error in Directions API call: " + e.getMessage()); //TODO: Visualizzare errore nella GUI
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelModeResult getTravelModeMainResult() {
        return this.directions.getMainResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TravelModeResult> getAlternativesResults() {
        return this.directions.getAlternativeResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelRequest finalizeTheRequest() {
        try {
            return this.requestBuilder.build();
        } catch (final IllegalStateException e){
            System.out.println(e.getMessage()); //TODO: migliore gestione dell'errore
        }
        return null;
    }
}
