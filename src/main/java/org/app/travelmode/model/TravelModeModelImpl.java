package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.app.travelmode.directions.DirectionsResponse;
import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TravelModeModelImpl implements TravelModeModel {

    private final PlaceAutocomplete placeAutocomplete;
    private final TravelRequestImpl.Builder requestBuilder;
    private final RouteAnalyzer routeAnalyzer;
    private String googleApiKey;
    private DirectionsResponse directionsResponse;

    public TravelModeModelImpl() {
        this.placeAutocomplete = new PlaceAutocompleteImpl();
        this.requestBuilder = new TravelRequestImpl.Builder();
        this.routeAnalyzer = new RouteAnalyzerImpl();
        //TODO: integrare in json reader
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        return this.placeAutocomplete.getPlacePredictions(input);
    }

    @Override
    public TravelRequestImpl.Builder setDepartureLocation(final String departureLocation) {
        return this.requestBuilder.setDepartureLocation(departureLocation);
    }

    @Override
    public TravelRequestImpl.Builder setDeparturePlaceId(final String departurePlaceId) {
        return this.requestBuilder.setDeparturePlaceId(departurePlaceId);
    }

    @Override
    public TravelRequestImpl.Builder setArrivalLocation(final String arrivalLocation) {
        return this.requestBuilder.setArrivalLocation(arrivalLocation);
    }

    @Override
    public TravelRequestImpl.Builder setArrivalPlaceId(final String arrivalPlaceId) {
        return this.requestBuilder.setArrivalPlaceId(arrivalPlaceId);
    }

    @Override
    public TravelRequestImpl.Builder setDepartureTime(final LocalTime departureTime) {
        return this.requestBuilder.setDepartureTime(departureTime);
    }

    @Override
    public TravelRequestImpl.Builder setDepartureDate(final LocalDate departureDate) {
        return this.requestBuilder.setDepartureDate(departureDate);
    }

    @Override
    public void startRouteAnalysis() {
        this.requestRoute(this.requestBuilder.build());
        for (final DirectionsRoute route : this.directionsResponse.getRoutes()) {
            this.routeAnalyzer.calculateIntermediatePoints(route);
        }
    }

    //TODO: delegare ad advanced json reader
    private void requestRoute(TravelRequest travelRequest) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
