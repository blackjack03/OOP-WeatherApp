package org.app.travelmode.model.checkpoint.impl;

import org.app.travelmode.model.checkpoint.api.Checkpoint;

import java.time.ZonedDateTime;

/**
 * Implementation of the {@link Checkpoint} interface representing a geographical waypoint
 * with coordinates in decimal degrees and arrival time information.
 */
public class CheckpointImpl implements Checkpoint {

    private final double latitude;
    private final double longitude;
    private final ZonedDateTime arrivalDateTime;

    /**
     * Constructs a new checkpoint with specified coordinates and arrival time.
     *
     * @param latitude        the latitude in decimal degrees where:
     *                        <ul>
     *                            <li>Positive values represent North latitude (0° to +90°)</li>
     *                            <li>Negative values represent South latitude (0° to -90°)</li>
     *                        </ul>
     * @param longitude       the longitude in decimal degrees where:
     *                        <ul>
     *                            <li>Positive values represent East longitude (0° to +180°)</li>
     *                            <li>Negative values represent West longitude (0° to -180°)</li>
     *                        </ul>
     * @param arrivalDateTime the expected or actual arrival date and time at this checkpoint
     */
    public CheckpointImpl(double latitude, double longitude, final ZonedDateTime arrivalDateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalDateTime = arrivalDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime getArrivalDateTime() {
        return this.arrivalDateTime;
    }

    /**
     * Returns a string representation of this checkpoint.
     *
     * @return a string containing the latitude, longitude, and arrival timestamp
     */
    @Override
    public String toString() {
        return "GeoPoint{" +
                "latitude=" + this.latitude +
                ", longitude=" + this.longitude +
                ", timestamp=" + arrivalDateTime +
                '}';
    }
}
