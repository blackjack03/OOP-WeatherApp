package org.app.travelmode.directions;

/**
 * A place on Earth, represented by a Latitude/Longitude pair.
 */
public class LatLng {

    private final double lat;
    private final double lng;

    /**
     * Construct a location with a latitude longitude pair.
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
    public String toString() {
        return "{Lat: " + lat + ", Lng: " + lng + "}";
    }
}
