package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;
import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;
import org.app.travelmode.directions.DirectionsLeg;
import org.app.travelmode.directions.DirectionsResponse;
import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.directions.SimpleDirectionsStep;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TravelModeModelImpl implements TravelModeModel {

    private final PlaceAutocomplete placeAutocomplete;
    private final TravelRequestImpl.Builder requestBuilder;
    private final List<TravelModeResult> results;
    private Optional<DirectionsResponse> directionsResponse;
    private String googleApiKey;

    //TODO: da eliminare
    private WeatherCondition weatherCondition = new WeatherConditionImpl(WeatherType.THUNDERSTORM, Intensity.HIGH);
    private WeatherCondition weatherCondition2 = new WeatherConditionImpl(WeatherType.FOG, Intensity.HIGH);
    private WeatherCondition weatherCondition3 = new WeatherConditionImpl(WeatherType.HAIL, Intensity.HIGH);
    private WeatherCondition weatherCondition4 = new WeatherConditionImpl(WeatherType.THUNDERSTORM, Intensity.HIGH);
    private List<WeatherCondition> weatherConditions = List.of(weatherCondition, weatherCondition3, weatherCondition4);
    private WeatherReport weatherReport = new WeatherReportImpl();

    public TravelModeModelImpl() {

        //TODO: eliminare
        for (final WeatherCondition weatherCondition : weatherConditions) {
            weatherReport.addCondition(weatherCondition);
        }


        this.placeAutocomplete = new PlaceAutocompleteImpl();
        this.requestBuilder = new TravelRequestImpl.Builder();
        //TODO: integrare in json reader
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.results = new ArrayList<>();
    }

    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        return this.placeAutocomplete.getPlacePredictions(input);
    }

    @Override
    public void setDepartureLocation(final String departureLocation) {
        this.requestBuilder.setDepartureLocation(departureLocation);
    }

    @Override
    public void setDeparturePlaceId(final String departurePlaceId) {
        final String placeDetailsUrl = "https://maps.googleapis.com/maps/api/place/details/json" +
                "?fields=utc_offset" +
                "&place_id=" + departurePlaceId +
                "&key=" + googleApiKey;
        final ZoneId departureZoneId;
        try { //TODO: da migliorare
            final AdvancedJsonReader jsonReader = new AdvancedJsonReaderImpl(placeDetailsUrl);
            int utcOffset = jsonReader.getInt("result\\utc_offset");
            departureZoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(utcOffset * 60));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(departureZoneId);
        this.requestBuilder.setDeparturePlaceId(departurePlaceId).setDepartureZoneId(departureZoneId);
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
    public void startRouteAnalysis() {
        this.results.clear();
        final TravelRequest travelRequest = this.requestBuilder.build();
        this.directionsResponse = Optional.ofNullable(requestRoute(travelRequest));
        final DirectionsResponse directionResult;
        try {
            directionResult = this.directionsResponse.get();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("Il servizio di calcolo delle indicazioni stradali non ha fornito alcun risultato");
        }

        final RouteAnalyzer routeAnalyzer = new RouteAnalyzerImpl(new IntermediatePointFinderImpl(), new SubStepGeneratorImpl());
        for (final DirectionsRoute route : directionResult.getRoutes()) {
            final List<SimpleDirectionsStep> intermediatePoints = routeAnalyzer.calculateIntermediatePoints(route);
            System.out.println(intermediatePoints);
            final List<Checkpoint> checkpoints = generateCheckpoints(intermediatePoints, travelRequest);
            System.out.println(checkpoints);
            final List<CheckpointWithMeteo> checkpointWithMeteos = new ArrayList<>();
            for (final Checkpoint checkpoint : checkpoints) { //TODO: da rivedere
                checkpointWithMeteos.add(new CheckpointWithMeteoImpl(checkpoint.getLatitude(), checkpoint.getLongitude(), checkpoint.getArrivalDateTime(), weatherReport));
            }
            this.results.add(new TravelModeResultImpl(checkpointWithMeteos, route.getSummary(), route.getOverview_polyline().getPoints(), calculateRouteDuration(route)));
        }
    }

    private List<Checkpoint> generateCheckpoints(final List<SimpleDirectionsStep> steps, final TravelRequest travelRequest) {
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

    private Duration calculateRouteDuration(final DirectionsRoute route) {
        double totalDuration = 0;
        for (final DirectionsLeg leg : route.getLegs()) {
            totalDuration += leg.getDuration().getValue();
        }
        return Duration.ofSeconds((long) totalDuration);
    }

    //TODO: delegare ad advanced json reader
    private DirectionsResponse requestRoute(final TravelRequest travelRequest) {
        final String urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                "?destination=place_id%3A" + travelRequest.getArrivalLocationPlaceId() +
                "&origin=place_id%3A" + travelRequest.getDepartureLocationPlaceId() +
                "&departure_time=" + travelRequest.getDepartureDateTime().toEpochSecond() +
                "&alternatives=true" +
                "&language=it" +
                "&units=metric" +
                "&key=" + googleApiKey;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            final StringBuilder response = new StringBuilder();
            if (responseCode == 200) { // 200 OK
                final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
            } else {
                throw new Exception("Errore nella richiesta: " + responseCode);
            }

            System.out.println(response.toString());
            final Gson gson = new Gson();
            return gson.fromJson(response.toString(), DirectionsResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Image getStaticMap() {
        return this.results.get(0).getMapImage();
    }

    @Override
    public TravelModeResult getTravelModeMainResult() {
        return this.results.get(0);
    }
}
