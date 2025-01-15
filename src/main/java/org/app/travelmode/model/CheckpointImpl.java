package org.app.travelmode.model;

import java.time.ZonedDateTime;

public class CheckpointImpl implements Checkpoint {

    private final double latitude;
    private final double longitude;
    private final ZonedDateTime arrivalDateTime;

    public CheckpointImpl(double latitude, double longitude, final ZonedDateTime arrivalDateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalDateTime = arrivalDateTime;
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public ZonedDateTime getArrivalDateTime() {
        return this.arrivalDateTime;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "latitude=" + this.latitude +
                ", longitude=" + this.longitude +
                ", timestamp=" + arrivalDateTime +
                '}';
    }
}
