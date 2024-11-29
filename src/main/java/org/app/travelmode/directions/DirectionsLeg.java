package org.app.travelmode.directions;

import java.util.List;

public class DirectionsLeg {

    private String end_address;
    private LatLngLiteral end_location;
    private String start_address;
    private LatLngLiteral start_location;
    private List<DirectionsStep> steps;
    private TextValueObject distance;
    private TextValueObject duration;
    private TextValueObject duration_in_traffic;

    public DirectionsLeg() {
    }

    public String getEnd_address() {
        return this.end_address;
    }

    public LatLngLiteral getEnd_location() {
        return this.end_location;
    }

    public String getStart_address() {
        return this.start_address;
    }

    public LatLngLiteral getStart_location() {
        return this.start_location;
    }

    public List<DirectionsStep> getSteps() {
        return this.steps;
    }

    public TextValueObject getDistance() {
        return this.distance;
    }

    public TextValueObject getDuration() {
        return this.duration;
    }

    public TextValueObject getDuration_in_traffic() {
        return this.duration_in_traffic;
    }

    @Override
    public String toString() {
        return "{DirectionsLeg [end_address=" + end_address + ",\n end_location=" + end_location +
                ",\n start_address=" + start_address + ",\n start_location=" + start_location +
                ",\n distance=" + distance + ",\n duration=" + duration + ",\n duration_in_traffic=" + duration_in_traffic +
                ",\n steps=" + steps + "\n}";
    }
}
