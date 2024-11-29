package org.app.travelmode.directions;

public class DirectionsStep {

    private TextValueObject duration;
    private LatLngLiteral end_location;
    private DirectionsPolyline polyline;
    private LatLngLiteral start_location;
    private String travel_mode;
    private TextValueObject distance;


    public TextValueObject getDuration() {
        return this.duration;
    }

    public LatLngLiteral getEnd_location() {
        return this.end_location;
    }

    public DirectionsPolyline getPolyline() {
        return this.polyline;
    }

    public LatLngLiteral getStart_location() {
        return this.start_location;
    }

    public String getTravel_mode() {
        return this.travel_mode;
    }

    public TextValueObject getDistance() {
        return this.distance;
    }

    @Override
    public String toString() {
        return "DirectionsStep{" + "duration= " + duration +
                ",\ndistance= " + distance +
                ",\nstart_location= " + start_location +
                ",\nend_location= " + end_location +
                ",\npolyline= " + polyline +
                ",\ntravel_mode= " + travel_mode;
    }
}
