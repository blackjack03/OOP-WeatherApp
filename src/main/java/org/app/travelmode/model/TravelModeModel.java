package org.app.travelmode.model;

import javafx.scene.image.Image;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TravelModeModel {

    /**
     * Provides a list of {@link PlaceAutocompletePrediction} based on the input text
     *
     * @param input String representing the characters written by the user
     * @return a list of predictions based on the input
     */
    List<PlaceAutocompletePrediction> getPlacePredictions(final String input);

    /**
     * Set the departure location
     *
     * @param departureLocation the name of the departure location
     */
    void setDepartureLocation(final String departureLocation);

    /**
     * Set the PlaceId associated with the starting location
     *
     * @param departurePlaceId the PlaceId associated with the starting location
     */
    void setDeparturePlaceId(final String departurePlaceId);

    /**
     * Set the arrival location
     *
     * @param arrivalLocation the name of the arrival location
     */
    void setArrivalLocation(final String arrivalLocation);

    /**
     * Set the PlaceId associated with the arrival location
     *
     * @param arrivalPlaceId the PlaceId associated with the arrival location
     */
    void setArrivalPlaceId(final String arrivalPlaceId);

    /**
     * Set the departure time
     *
     * @param departureTime the departure time
     */
    void setDepartureTime(final LocalTime departureTime);

    /**
     * Set the departure date
     *
     * @param departureDate the departure date
     */
    void setDepartureDate(final LocalDate departureDate);

    /**
     * Start the analysis of possible routes between the inserted places
     */
    void startDirectionsAnalysis(TravelRequest travelRequest);

    Image getStaticMap();

    /**
     * @return a {@link TravelModeResult} object representing the results from analyzing the first route recommended by the routing service
     */
    TravelModeResult getTravelModeMainResult();

    /**
     * Returns a list of alternative route results after analyzing the navigation service's response.
     *
     * @return a {@link List} of {@link TravelModeResult} for alternative routes.
     */
    List<TravelModeResult> getAlternativesResults();

    /**
     * Builds a {@link TravelRequest} based on the information entered by the user.
     *
     * @return {@link TravelRequest} object.
     */
    TravelRequest finalizeTheRequest();
}
