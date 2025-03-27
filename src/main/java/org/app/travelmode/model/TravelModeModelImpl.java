package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;
import org.app.travelmode.directions.DirectionsResponse;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.io.FileReader;
import java.time.*;
import java.util.List;
import java.util.Optional;

public class TravelModeModelImpl implements TravelModeModel {

    private final PlaceAutocomplete placeAutocomplete;
    private final TravelRequestImpl.Builder requestBuilder;
    private Directions directions;
    private Optional<DirectionsResponse> directionsResponse;
    private String googleApiKey;


    public TravelModeModelImpl() {
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
            int utcOffset = jsonReader.getInt("result.utc_offset");
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
    public void startDirectionsAnalysis(final TravelRequest travelRequest) {
        this.directions = new DirectionsImpl(travelRequest);
        directions.askForDirections();
    }

    @Override
    public TravelModeResult getTravelModeMainResult() {
        return this.directions.getMainResult();
    }

    @Override
    public List<TravelModeResult> getAlternativesResults() {
        return this.directions.getAlternativeResults();
    }

    @Override
    public TravelRequest finalizeTheRequest() {
        return this.requestBuilder.build();
    }
}
