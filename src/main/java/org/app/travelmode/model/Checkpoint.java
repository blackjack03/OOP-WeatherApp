package org.app.travelmode.model;

import java.time.ZonedDateTime;

public interface Checkpoint {

    /**
     * Allows you to get the latitude of this checkpoint
     *
     * @return the latitude of this checkpoint
     */
    double getLatitude();

    /**
     * Allows you to get the longitude of this checkpoint
     *
     * @return the longitude of this checkpoint
     */
    double getLongitude();

    /**
     * Allows you to get a {@link ZonedDateTime} that represents the expected arrival time at the checkpoint
     *
     * @return a {@link ZonedDateTime} that represents the expected arrival time at this checkpoint
     */
    ZonedDateTime getArrivalDateTime();
}
