package org.app.travelmode.directions;

public class LatLngLiteral {

    private final double lat;
    private final double lng;

    public LatLngLiteral(double lat, double lng) {
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
