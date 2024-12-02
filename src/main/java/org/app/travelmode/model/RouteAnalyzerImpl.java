package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.app.travelmode.directions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RouteAnalyzerImpl implements RouteAnalyzer {

    private static final double DELTA = 2000.0;
    private String googleApiKey;
    private DirectionsResponse directionsResponse;

    public RouteAnalyzerImpl() {
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestRoute(TravelRequest travelRequest) {
        final String urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                "?destination=place_id%3A" + travelRequest.getArrivalLocationPlaceId() +
                "&origin=place_id%3A" + travelRequest.getDepartureLocationPlaceId() +
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
            directionsResponse = gson.fromJson(response.toString(), DirectionsResponse.class);
            System.out.println(directionsResponse);
            calculateIntermediatePoints(directionsResponse.getRoutes().get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LatLng> calculateIntermediatePoints(final DirectionsRoute directionsRoute) {
        final List<DirectionsLeg> legs = directionsRoute.getLegs();
        final List<LatLng> intermediatePoints = new ArrayList<>();
        double distanceCounter = 0;
        for (final DirectionsLeg leg : legs) {
            List<DirectionsStep> steps = leg.getSteps();
            for (final DirectionsStep step : steps) {
                distanceCounter += step.getDistance().getValue();
                if (distanceCounter > 30000 - DELTA && distanceCounter < 30000 + DELTA) {
                    intermediatePoints.add(step.getEnd_location());
                    distanceCounter = 0;
                } else if (distanceCounter >= 30000 + DELTA) {
                    distanceCounter -= step.getDistance().getValue();
                    for (final DirectionsStep subStep : generateSubSteps(step)) {
                        distanceCounter += subStep.getDistance().getValue();
                        if (distanceCounter > 30000 - DELTA && distanceCounter < 30000 + DELTA) {
                            intermediatePoints.add(step.getEnd_location());
                            distanceCounter = 0;
                        }
                    }
                    System.out.println(generateSubSteps(step));
                }
                System.out.println("[Durata stimata: " + step.getDuration() + ", Distanza: " + step.getDistance() + ", Punto iniziale: " + step.getStart_location() + ", Punto finale: " + step.getEnd_location() + "]");
            }
        }
        System.out.println(intermediatePoints);
        return intermediatePoints;
    }

    private List<DirectionsStep> generateSubSteps(final DirectionsStep directionsStep) {
        final List<DirectionsStep> subSteps = new ArrayList<>();
        final List<LatLng> decodedPoints = PolylineDecoder.decode(directionsStep.getPolyline().getPoints());

        LatLng startPoint = decodedPoints.get(0);
        double actualDistance = 0;
        int i;
        for (i = 1; i < decodedPoints.size(); i++) {
            LatLng actualPoint = decodedPoints.get(i);
            actualDistance = GeographicDistanceCalculator.computeDistance(startPoint, actualPoint);
            if (Double.compare(actualDistance, 1000.0) >= 0) {
                subSteps.add(new DirectionsStep(calculateSubStepDuration(directionsStep, actualDistance), actualPoint, startPoint, new TextValueObject(Double.toString(actualDistance), actualDistance)));
                actualDistance = 0;
                startPoint = actualPoint;
            }
        }
        if (actualDistance != 0) {
            subSteps.add(new DirectionsStep(calculateSubStepDuration(directionsStep, actualDistance), decodedPoints.get(decodedPoints.size() - 1), startPoint, new TextValueObject(Double.toString(actualDistance), actualDistance)));
        }
        return subSteps;
    }

    private TextValueObject calculateSubStepDuration(final DirectionsStep directionsStep, double subStepLength) {
        double subStepDuration = (directionsStep.getDuration().getValue() * subStepLength) / directionsStep.getDistance().getValue();
        return new TextValueObject(Double.toString(subStepDuration), subStepDuration);
    }
}
