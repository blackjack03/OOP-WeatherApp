package org.app.travelmode.model;

import java.time.ZoneId;

public interface PlaceDetails {

    /**
     * Allows you to obtain the time zone of a location.
     *
     * @param placeId A string representing the PlaceID of the location whose time zone you want to know.
     * @return A {@link ZoneId} object to represent the time zone.
     */
    ZoneId getTimezone(String placeId);

}
