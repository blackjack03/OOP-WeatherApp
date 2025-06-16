package org.app.travelmode.model.google.api;

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
     * @return the {@link ZoneId} for the specified place,
     * or null if the timezone cannot be determined or if an error occurs.
     */
    ZoneId getTimezone(String placeId);

}
