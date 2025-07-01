package org.app.travelmode.model.routing.impl;

import org.app.travelmode.model.analysis.api.CheckpointGenerator;
import org.app.travelmode.model.analysis.api.RouteAnalyzer;
import org.app.travelmode.model.analysis.api.WeatherInformationService;
import org.app.travelmode.model.analysis.impl.*;
import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.exception.WeatherDataException;
import org.app.travelmode.model.google.impl.DirectionApiClientImpl;
import org.app.travelmode.model.google.api.GoogleApiClientFactory;
import org.app.travelmode.model.google.impl.GoogleApiClientFactoryImpl;
import org.app.travelmode.model.google.dto.directions.*;

import org.app.travelmode.model.checkpoint.api.Checkpoint;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;

import org.app.travelmode.model.routing.api.Directions;
import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.impl.TravelModeResultImpl;
import org.app.travelmode.model.travel.api.TravelRequest;

import org.app.travelmode.model.weather.impl.WeatherConditionFactoryImpl;

import java.time.Duration;
import java.util.*;

/**
 * Implementation of the {@link Directions} interface that handles route calculations
 * and analysis with weather information integration.
 *
 * <p>This class provides functionality to:
 * <ul>
 *     <li>Request and process directions from Google Directions API</li>
 *     <li>Generate route checkpoints with weather information</li>
 *     <li>Handle multiple route alternatives</li>
 *     <li>Calculate route durations and intermediate points</li>
 * </ul>
 */
public class DirectionsImpl implements Directions {

    private TravelRequest travelRequest;
    private Optional<TravelModeResult> mainResult;
    private Optional<List<TravelModeResult>> alternativeResult;
    private Optional<DirectionsResponse> directionsResponse;


    /**
     * Creates a new DirectionsImpl instance with empty state.
     */
    public DirectionsImpl() {
        this.mainResult = Optional.empty();
        this.alternativeResult = Optional.empty();
        this.directionsResponse = Optional.empty();
    }

    /**
     * Creates a new DirectionsImpl instance with a specific travel request.
     *
     * @param travelRequest the initial travel request to process
     */
    public DirectionsImpl(final TravelRequest travelRequest) {
        this();
        this.travelRequest = travelRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTravelRequest(final TravelRequest travelRequest) {
        this.mainResult = Optional.empty();
        this.alternativeResult = Optional.empty();
        this.directionsResponse = Optional.empty();
        this.travelRequest = travelRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askForDirections() throws DirectionsApiException {
        if (travelRequest == null) {
            throw new IllegalStateException("Per ottenere una risposta dall'api Directions è necessario impostare una TravelRequest");
        }
        this.askForDirections(this.travelRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askForDirections(final TravelRequest travelRequest) throws DirectionsApiException {
        final GoogleApiClientFactory googleApiClientFactory = new GoogleApiClientFactoryImpl();
        final DirectionApiClientImpl directionApiClient = googleApiClientFactory.createDirectionApiClient();
        this.directionsResponse = Optional.of(directionApiClient.getDirections(travelRequest));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public TravelModeResult getMainResult() throws WeatherDataException {
        if (this.mainResult.isEmpty()) {
            final DirectionsResponse directionResult = getDirectionsResponse();
            this.mainResult = Optional.of(analyzeRoute(directionResult.getRoutes().get(0)));
        }
        return this.mainResult.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<List<TravelModeResult>> getAlternativeResults() throws WeatherDataException {
        final DirectionsResponse directionResult = getDirectionsResponse();
        if (this.alternativeResult.isEmpty() && directionResult.hasAlternatives()) {
            final List<DirectionsRoute> routes = directionResult.getRoutes();
            final List<TravelModeResult> results = new ArrayList<>();
            for (int i = 1; i < routes.size(); i++) {
                results.add(analyzeRoute(routes.get(i)));
            }
            this.alternativeResult = Optional.of(results);
        }
        return this.alternativeResult;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if called before making a successful request
     *                               to the Directions API or if the response is not present
     */
    @Override
    public DirectionsResponse getDirectionsResponse() {
        if (this.directionsResponse.isPresent()) {
            return directionsResponse.get();
        } else {
            throw new IllegalStateException("DirectionsResponse non presente");
        }
    }

    /**
     * Analyzes a route to create a detailed travel result with weather information.
     *
     * <p>The analysis process includes:
     * <ul>
     *     <li>Calculating intermediate points along the route</li>
     *     <li>Generating checkpoints with timing information</li>
     *     <li>Enriching checkpoints with weather data</li>
     *     <li>Computing total route duration</li>
     * </ul>
     *
     * @param route the {@link DirectionsRoute} to analyze
     * @return a complete {@link TravelModeResult} with route and weather information
     * @throws WeatherDataException if an error occurs while receiving weather information.
     */
    private TravelModeResult analyzeRoute(final DirectionsRoute route) throws WeatherDataException {
        final RouteAnalyzer routeAnalyzer = new RouteAnalyzerImpl(new IntermediatePointFinderImpl(), new SubStepGeneratorImpl());
        final CheckpointGenerator checkpointGenerator = new CheckpointGeneratorImpl();

        final List<SimpleDirectionsStep> intermediatePoints = routeAnalyzer.calculateIntermediatePoints(route);

        final List<Checkpoint> checkpoints = checkpointGenerator.generateCheckpoints(intermediatePoints, this.travelRequest.getDepartureDateTime());

        final WeatherInformationService weatherInformationService = new WeatherInformationServiceImpl(new WeatherConditionFactoryImpl());
        final List<CheckpointWithMeteo> checkpointsWithMeteo = new ArrayList<>();
        for (final Checkpoint checkpoint : checkpoints) {
            checkpointsWithMeteo.add(weatherInformationService.enrichWithWeather(checkpoint));
        }

        return new TravelModeResultImpl(
                checkpointsWithMeteo,
                route.getSummary(),
                route.getOverview_polyline().getPoints(),
                calculateRouteDuration(route)
        );
    }

    /**
     * Calculates the total duration of a route.
     *
     * @param route the {@link DirectionsRoute} whose duration needs to be calculated.
     * @return a {@link Duration} representing the total route duration.
     */
    private Duration calculateRouteDuration(final DirectionsRoute route) {
        final double totalDuration = route.getLegs().stream()
                .mapToDouble(leg -> leg.getDuration().getValue())
                .sum();

        return Duration.ofSeconds((long) totalDuration);
    }

}
