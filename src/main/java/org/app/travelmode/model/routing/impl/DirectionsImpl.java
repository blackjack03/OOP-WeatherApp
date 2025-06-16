package org.app.travelmode.model;

import org.app.travelmode.model.google.impl.DirectionApiClientImpl;
import org.app.travelmode.model.google.api.GoogleApiClientFactory;
import org.app.travelmode.model.google.impl.GoogleApiClientFactoryImpl;
import org.app.travelmode.model.google.dto.directions.*;

import org.app.travelmode.model.checkpoint.api.Checkpoint;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;

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
    public void askForDirections() {
        if (travelRequest == null) {
            throw new IllegalStateException("Per ottenere una risposta dall'api Directions è necessario impostare una TravelRequest");
        }
        askForDirections(this.travelRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askForDirections(final TravelRequest travelRequest) {
        final GoogleApiClientFactory googleApiClientFactory = new GoogleApiClientFactoryImpl();
        final DirectionApiClientImpl directionApiClient = googleApiClientFactory.createDirectionApiClient();
        this.directionsResponse = Optional.of(directionApiClient.getDirections(travelRequest));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TravelModeResult getMainResult() {
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
    public List<TravelModeResult> getAlternativeResults() {
        if (this.alternativeResult.isPresent()) {
            return List.copyOf(this.alternativeResult.get());
        } else {
            final DirectionsResponse directionResult = getDirectionsResponse();
            final List<DirectionsRoute> routes = directionResult.getRoutes();
            if (routes.size() > 1) {
                final List<TravelModeResult> results = new ArrayList<>();
                for (int i = 1; i < routes.size(); i++) {
                    results.add(analyzeRoute(routes.get(i)));
                }
                this.alternativeResult = Optional.of(results);
                return List.copyOf(results);
            } else {
                throw new UnsupportedOperationException("Non sono disponibili percorsi alternativi");
            }
        }
    }

    /**
     * {@inheritDoc}
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
     */
    private TravelModeResult analyzeRoute(final DirectionsRoute route) {
        final RouteAnalyzer routeAnalyzer = new RouteAnalyzerImpl(new IntermediatePointFinderImpl(), new SubStepGeneratorImpl());
        final CheckpointGenerator checkpointGenerator = new CheckpointGeneratorImpl();

        final List<SimpleDirectionsStep> intermediatePoints = routeAnalyzer.calculateIntermediatePoints(route);
        System.out.println(intermediatePoints);

        final List<Checkpoint> checkpoints = checkpointGenerator.generateCheckpoints(intermediatePoints, this.travelRequest.getDepartureDateTime());
        System.out.println(checkpoints);

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
        double totalDuration = 0;
        for (final DirectionsLeg leg : route.getLegs()) {
            totalDuration += leg.getDuration().getValue();
        }
        return Duration.ofSeconds((long) totalDuration);
    }

}
