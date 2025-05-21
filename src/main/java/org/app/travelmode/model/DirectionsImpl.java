package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsLeg;
import org.app.travelmode.directions.DirectionsResponse;
import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.time.Duration;
import java.util.*;

public class DirectionsImpl implements Directions {

    private TravelRequest travelRequest;
    private Optional<TravelModeResult> mainResult;
    private Optional<List<TravelModeResult>> alternativeResult;
    private Optional<DirectionsResponse> directionsResponse;


    public DirectionsImpl() {
        this.mainResult = Optional.empty();
        this.alternativeResult = Optional.empty();
        this.directionsResponse = Optional.empty();
    }

    public DirectionsImpl(final TravelRequest travelRequest) {
        this();
        this.travelRequest = travelRequest;
    }

    @Override
    public void setTravelRequest(final TravelRequest travelRequest) {
        this.mainResult = Optional.empty();
        this.alternativeResult = Optional.empty();
        this.directionsResponse = Optional.empty();
        this.travelRequest = travelRequest;
    }

    @Override
    public void askForDirections() {
        if (travelRequest == null) {
            throw new IllegalStateException("Per ottenere una risposta dall'api Directions è necessario impostare una TravelRequest");
        }
        askForDirections(this.travelRequest);
    }

    @Override
    public void askForDirections(final TravelRequest travelRequest) {
        final GoogleApiClientFactory googleApiClientFactory = new GoogleApiClientFactoryImpl();
        final DirectionApiClient directionApiClient = googleApiClientFactory.createDirectionApiClient();
        this.directionsResponse = Optional.of(directionApiClient.getDirections(travelRequest));
    }

    @Override
    public TravelModeResult getMainResult() {
        if (this.mainResult.isEmpty()) {
            final DirectionsResponse directionResult = getDirectionsResponse();
            this.mainResult = Optional.of(analyzeRoute(directionResult.getRoutes().get(0)));
        }
        return this.mainResult.get();
    }

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

    @Override
    public DirectionsResponse getDirectionsResponse() {
        if (this.directionsResponse.isPresent()) {
            return directionsResponse.get();
        } else {
            throw new IllegalStateException("DirectionsResponse non presente");
        }
    }

    /**
     * Analyzes a specific route and generates a {@link TravelModeResult}.
     *
     * @param route the {@link DirectionsRoute} to analyze.
     * @return the {@link TravelModeResult} for the analyzed route.
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
     * @param route the {@link DirectionsRoute} to analyze.
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
