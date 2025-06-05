package org.app.travelmode.model;

import java.time.ZonedDateTime;

/**
 * Represents a geographical checkpoint with coordinates and arrival time information.
 *
 * <p>A checkpoint defines a specific geographical location using coordinates
 * in decimal degrees, along with an expected or actual arrival time at that location.
 */
public interface Checkpoint {

    /**
     * Allows you to get the latitude of this checkpoint
     *
     * @return the latitude of this checkpoint in decimal degrees
     */
    double getLatitude();

    /**
     * Allows you to get the longitude of this checkpoint
     *
     * @return the longitude of this checkpoint in decimal degrees
     */
    double getLongitude();

    /**
     * Returns the expected arrival time at this checkpoint.
     *
     * @return a {@link ZonedDateTime} that represents the expected arrival time at this checkpoint
     */
    ZonedDateTime getArrivalDateTime();
}
