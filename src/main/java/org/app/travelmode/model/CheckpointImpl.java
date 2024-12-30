package org.app.travelmode.model;

import org.app.travelmode.directions.LatLng;

import java.time.ZonedDateTime;

public class CheckpointImpl implements Checkpoint {

    private final LatLng coordinates;
    private final ZonedDateTime arrivalDateTime;

    public CheckpointImpl(final LatLng coordinates, final ZonedDateTime arrivalDateTime) {
        this.coordinates = coordinates;
        this.arrivalDateTime = arrivalDateTime;
    }

    @Override
    public double getLatitude() {
        return this.coordinates.getLat();
    }

    @Override
    public double getLongitude() {
        return this.coordinates.getLng();
    }

    @Override
    public ZonedDateTime getArrivalDateTime() {
        return this.arrivalDateTime;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "latitude=" + coordinates.getLat() +
                ", longitude=" + coordinates.getLng() +
                ", timestamp=" + arrivalDateTime +
                '}';
    }
}
