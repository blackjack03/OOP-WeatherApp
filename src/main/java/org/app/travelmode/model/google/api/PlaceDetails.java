package org.app.travelmode.model.google.api;

import java.io.IOException;
import java.time.ZoneId;

/**
 * Defines a contract for retrieving detailed information about places,
 * specifically focusing on timezone data.
 */
public interface PlaceDetails {

    /**
     * Retrieves the timezone information for a specific place.
     *
     * @param placeId A string representing the PlaceID of the location whose time zone you want to know.
     * @return a {@link ZoneId} representing the timezone of the specified location.
     * @throws IOException if there's an error communicating with the Google Places API
     *                     or parsing the response
     */
    ZoneId getTimezone(String placeId) throws IOException;

}
