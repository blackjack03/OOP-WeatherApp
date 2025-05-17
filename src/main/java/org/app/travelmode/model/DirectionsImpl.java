package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;
import org.app.model.Weather;
import org.app.travelmode.directions.DirectionsLeg;
import org.app.travelmode.directions.DirectionsResponse;
import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.io.FileReader;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

public class DirectionsImpl implements Directions {

    private TravelRequest travelRequest;
    private Optional<TravelModeResult> mainResult;
    private Optional<List<TravelModeResult>> alternativeResult;
    private Optional<DirectionsResponse> directionsResponse;
    private String googleApiKey;


    public DirectionsImpl() {

        this.mainResult = Optional.empty();
        this.alternativeResult = Optional.empty();
        this.directionsResponse = Optional.empty();

        //TODO: integrare in json reader
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        final String urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                "?destination=place_id%3A" + travelRequest.getArrivalLocationPlaceId() +
                "&origin=place_id%3A" + travelRequest.getDepartureLocationPlaceId() +
                "&departure_time=" + travelRequest.getDepartureDateTime().toEpochSecond() +
                "&alternatives=true" +
                "&language=it" +
                "&units=metric" +
                "&key=" + googleApiKey;

        final AdvancedJsonReader jsonReader = new AdvancedJsonReaderImpl();

        try {
            jsonReader.requestJSON(urlString);
            final String rawJSon = jsonReader.getRawJSON();
            System.out.println(rawJSon);

            final Gson gson = new Gson();
            this.directionsResponse = Optional.of(gson.fromJson(rawJSon, DirectionsResponse.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        final List<SimpleDirectionsStep> intermediatePoints = routeAnalyzer.calculateIntermediatePoints(route);
        System.out.println(intermediatePoints);

        final List<Checkpoint> checkpoints = generateCheckpoints(intermediatePoints);
        System.out.println(checkpoints);

        final List<CheckpointWithMeteo> checkpointsWithMeteo = new ArrayList<>();
        for (final Checkpoint checkpoint : checkpoints) { //TODO: da rivedere
            checkpointsWithMeteo.add(this.addWeatherInformation(checkpoint));
        }

        return new TravelModeResultImpl(checkpointsWithMeteo, route.getSummary(), route.getOverview_polyline().getPoints(), calculateRouteDuration(route));
    }

    /**
     * Generates a list of checkpoints from a list of directions steps.
     *
     * @param steps the list of {@link SimpleDirectionsStep} to process.
     * @return a {@link List} of {@link Checkpoint}.
     */
    private List<Checkpoint> generateCheckpoints(final List<SimpleDirectionsStep> steps) {
        final List<Checkpoint> checkpoints = new ArrayList<>();
        final SimpleDirectionsStep firstStep = steps.get(0);
        double latitude = firstStep.getStart_location().getLat();
        double longitude = firstStep.getStart_location().getLng();
        checkpoints.add(new CheckpointImpl(latitude, longitude, travelRequest.getDepartureDateTime()));
        for (int i = 0; i < steps.size(); i++) {
            final SimpleDirectionsStep step = steps.get(i);
            latitude = step.getEnd_location().getLat();
            longitude = step.getEnd_location().getLng();
            long duration = (long) step.getDuration().getValue();
            checkpoints.add(new CheckpointImpl(latitude, longitude, checkpoints.get(i).getArrivalDateTime().plusSeconds(duration)));
        }
        return checkpoints;
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

    private CheckpointWithMeteo addWeatherInformation(final Checkpoint checkpoint) {
        final Map<String, String> coordinates = Map.of(
                "lat", String.valueOf(checkpoint.getLatitude()),
                "lng", String.valueOf(checkpoint.getLongitude())
        );

        final ZonedDateTime arrivalDateTime = checkpoint.getArrivalDateTime();
        final String arrivalHour = String.format("%d:%d", arrivalDateTime.getHour(), arrivalDateTime.getMinute());

        final Weather weather = new Weather(coordinates);
        final Map<String, Number> weatherInformation = weather.getWeatherOn(
                arrivalDateTime.getDayOfMonth(),
                arrivalDateTime.getMonthValue(),
                arrivalDateTime.getYear(),
                arrivalHour).orElseThrow(() -> new IllegalStateException("Impossibile ottenere le informazioni meteo"));

        final WeatherConditionFactory weatherConditionFactory = new WeatherConditionFactoryImpl();
        final List<WeatherCondition> weatherConditions = Arrays.asList(
                weatherConditionFactory.createFreezingRisk(weatherInformation.get("freezing_level_height").doubleValue()),
                weatherConditionFactory.createSnowfall(weatherInformation.get("snowfall").doubleValue()),
                weatherConditionFactory.createPrecipitation(weatherInformation.get("precipitation").doubleValue()),
                weatherConditionFactory.createVisibility(weatherInformation.get("visibility").doubleValue()),
                weatherConditionFactory.createWindGust(weatherInformation.get("wind_gusts").doubleValue())
        );

        final WeatherReport weatherReport = new WeatherReportImpl();
        weatherReport.addConditions(weatherConditions);

        return new CheckpointWithMeteoImpl(checkpoint.getLatitude(), checkpoint.getLongitude(), arrivalDateTime, weatherReport);
    }
}
