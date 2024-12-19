package org.app.travelmode.directions;

import java.util.Objects;

/**
 * A place on Earth, represented by a Latitude/Longitude pair.
 */
public class LatLng {

    private final double lat;
    private final double lng;

    /**
     * Construct a location with a latitude longitude pair.
     *
     * @param lat latitude
     * @param lng longitude
     */
    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LatLng tmp = (LatLng) obj;
        return Objects.equals(lat, tmp.lat) && Objects.equals(lng, tmp.lng);
    }

    @Override
    public String toString() {
        return "{Lat: " + lat + ", Lng: " + lng + "}";
    }
}
