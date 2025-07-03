package org.app.travelmode.model.google.dto.directions;

import java.util.Objects;

/**
 * {@code LatLng} represents a geographical coordinate with a latitude and longitude.
 * <p>
 * This class is used to model a point on Earth.
 * It provides access to latitude and longitude values and supports equality checks and hashing.
 * </p>
 */
public class LatLng {

    private final double lat;
    private final double lng;

    /**
     * Constructs a new {@code LatLng} object with the specified latitude and longitude.
     *
     * @param lat The latitude component of the coordinate
     * @param lng The longitude component of the coordinate
     */
    public LatLng(final double lat, final double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Returns the latitude value of this coordinate.
     *
     * @return The latitude as a {@code double}
     */
    public double getLat() {
        return this.lat;
    }

    /**
     * Returns the longitude value of this coordinate.
     *
     * @return The longitude as a {@code double}
     */
    public double getLng() {
        return this.lng;
    }

    /**
     * Computes a hash code based on latitude and longitude.
     *
     * @return An integer hash code for this coordinate
     */
    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

    /**
     * Compares this {@code LatLng} to another object for equality.
     * Returns {@code true} if the other object is also a {@code LatLng}
     * with the same latitude and longitude.
     *
     * @param obj The object to compare to
     * @return {@code true} if equal, {@code false} otherwise
     */
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

    /**
     * Returns a string representation of this coordinate in the format:
     * {@code {Lat: <latitude>, Lng: <longitude>}}.
     *
     * @return A string describing this coordinate
     */
    @Override
    public String toString() {
        return "{Lat: " + lat + ", Lng: " + lng + "}";
    }
}
