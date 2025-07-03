package org.app.travelmode.model.core;

import org.app.common.api.weather.WeatherDataProvider;
import org.app.travelmode.model.analysis.api.WeatherInformationService;
import org.app.travelmode.model.analysis.impl.WeatherInformationServiceImpl;
import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.exception.TravelRequestException;
import org.app.travelmode.model.exception.WeatherDataException;
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
import org.app.travelmode.model.weather.impl.WeatherConditionFactoryImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the TravelModeModel interface that handles trip planning and route analysis functionality
 * to check weather conditions that may be encountered during the trip.
 */
public class TravelModeModelImpl implements TravelModeModel {

    private final WeatherInformationService weatherInformationService;
    private final TravelRequestImpl.Builder requestBuilder;
    private final GoogleApiClientFactory apiClientFactory;
    private PlacePredictionsApiClient placePredictionsApiClient;
    private Directions directions;

    /**
     * Constructs a new TravelModeModelImpl instance.
     *
     * <p>Initializes the model with:
     * <ul>
     *     <li>A new travel request builder for creating validated travel requests</li>
     *     <li>A Google API client factory for creating various API clients</li>
     * </ul>
     *
     * <p>Note: The {@link #start()} method must be called after construction
     * to initialize the place predictions API client.
     *
     * @param weatherDataProvider the {@link WeatherDataProvider} to use for weather information.
     */
    public TravelModeModelImpl(final WeatherDataProvider weatherDataProvider) {
        this.weatherInformationService = new WeatherInformationServiceImpl(new WeatherConditionFactoryImpl(),
                weatherDataProvider);
        this.requestBuilder = new TravelRequestImpl.Builder();
        this.apiClientFactory = new GoogleApiClientFactoryImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        this.placePredictionsApiClient = this.apiClientFactory.createPlacePredictionsApiClient();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException if an error occurs while communicating with the service providing the predictions,
     *                     or if an error occurs while decoding the response.
     */
    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) throws IOException {
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
     *
     * @throws IOException if the time zone of the starting location cannot be obtained.
     */
    @Override
    public void setDeparturePlaceId(final String departurePlaceId) throws IOException {
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

    /**
     * {@inheritDoc}
     */
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
     *
     * @throws DirectionsApiException if an error occurs while requesting the desired routes.
     */
    @Override
    public void startDirectionsAnalysis(final TravelRequest travelRequest) throws DirectionsApiException {
        this.directions = new DirectionsImpl(this.weatherInformationService, travelRequest);
        directions.askForDirections();
    }

    /**
     * {@inheritDoc}
     *
     * @throws WeatherDataException if there is an error getting weather information for route analysis.
     */
    @Override
    public TravelModeResult getTravelModeMainResult() throws WeatherDataException {
        return this.directions.getMainResult();
    }

    /**
     * {@inheritDoc}
     *
     * @throws WeatherDataException if there is an error getting weather information for route analysis.
     */
    @Override
    public Optional<List<TravelModeResult>> getAlternativesResults() throws WeatherDataException {
        return this.directions.getAlternativeResults();
    }

    /**
     * {@inheritDoc}
     *
     * @throws TravelRequestException if not all the parameters needed to calculate the routes have been entered.
     */
    @Override
    public TravelRequest finalizeTheRequest() throws TravelRequestException {
        return this.requestBuilder.build();
    }
}
