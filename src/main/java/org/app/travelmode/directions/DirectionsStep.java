package org.app.travelmode.directions;

public class DirectionsStep {

    private TextValueObject duration;
    private LatLng end_location;
    private DirectionsPolyline polyline;
    private LatLng start_location;
    private String travel_mode;
    private TextValueObject distance;


    public TextValueObject getDuration() {
        return this.duration;
    }

    public LatLng getEnd_location() {
        return this.end_location;
    }

    public DirectionsPolyline getPolyline() {
        return this.polyline;
    }

    public LatLng getStart_location() {
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
