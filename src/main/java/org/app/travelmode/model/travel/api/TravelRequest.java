package org.app.travelmode.model.travel.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * It represents a request with all the elements necessary to calculate a trip between two places
 */
public interface TravelRequest {

    /**
     * Allows you to obtain the name of the departure location stored in this request
     *
     * @return the departure city stored in this request
     */
    String getDepartureLocation();

    /**
     * Allows you to obtain the PlaceId corresponding to the departure location
     *
     * @return the PlaceId corresponding to the departure location
     */
    String getDepartureLocationPlaceId();

    /**
     * Allows you to get the name of the arrival location stored in this request
     *
     * @return the name of the arrival location stored in this request
     */
    String getArrivalLocation();

    /**
     * Allows you to obtain the PlaceId corresponding to the arrival location
     *
     * @return the PlaceId corresponding to the arrival location
     */
    String getArrivalLocationPlaceId();

    /**
     * Allows you to get the departure time stored in this request
     *
     * @return the departure time stored in this request
     */
    LocalTime getDepartureTime();

    /**
     * Allows you to get the departure date stored in this request
     *
     * @return the departure date stored in this request
     */
    LocalDate getDepartureDate();

    /**
     * Allows you to get the time zone of the departure location
     *
     * @return the time zone of the departure location stored in this request
     */
    ZoneId getDepartureTimeZone();

    /**
     * Allows you to get the {@link ZonedDateTime} stored in this request
     *
     * @return the {@link ZonedDateTime} stored in this request
     */
    ZonedDateTime getDepartureDateTime();
}
