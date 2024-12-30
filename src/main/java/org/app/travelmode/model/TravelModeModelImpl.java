package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;
import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;
import org.app.travelmode.directions.DirectionsResponse;
import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
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
        this.routeAnalyzer = new RouteAnalyzerImpl(new IntermediatePointFinderImpl(), new SubStepGeneratorImpl());
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
            final AdvancedJsonReaderImpl jsonReader = new AdvancedJsonReader(placeDetailsUrl);
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
        this.requestRoute(this.requestBuilder.build());
        for (final DirectionsRoute route : this.directionsResponse.getRoutes()) {
            System.out.println(this.routeAnalyzer.calculateIntermediatePoints(route));
        }
    }

    //TODO: delegare ad advanced json reader
    private void requestRoute(final TravelRequest travelRequest) {
        final String urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                "?destination=place_id%3A" + travelRequest.getArrivalLocationPlaceId() +
                "&origin=place_id%3A" + travelRequest.getDepartureLocationPlaceId() +
                "&departure_time=" + travelRequest.getDepartureDateTime().toEpochSecond() +
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
            directionsResponse = gson.fromJson(response.toString(), DirectionsResponse.class);
            System.out.println(directionsResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Image getStaticMap() {
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400" + "&key=" + googleApiKey;
        try {
            // Connessione HTTP per ottenere l'immagine
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Controlla se la richiesta è stata eseguita con successo
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Errore durante la richiesta della mappa: " + connection.getResponseMessage());
            }

            // Ottieni l'immagine come InputStream
            InputStream inputStream = connection.getInputStream();

            // Crea un oggetto Image di JavaFX dall'InputStream
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
