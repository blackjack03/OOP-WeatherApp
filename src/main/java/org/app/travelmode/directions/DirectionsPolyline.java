package org.app.travelmode.directions;

public class DirectionsPolyline {
    private final String points;

    public DirectionsPolyline(final String points) {
        this.points = points;
    }

    public String getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "DirectionsPolyline [points= " + points + "]";
    }
}
