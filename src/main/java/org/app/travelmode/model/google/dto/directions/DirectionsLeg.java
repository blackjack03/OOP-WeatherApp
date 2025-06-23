package org.app.travelmode.model.google.dto.directions;

import java.util.List;

public class DirectionsLeg {

    private final String end_address;
    private final LatLng end_location;
    private final String start_address;
    private final LatLng start_location;
    private final List<DirectionsStep> steps;
    private final TextValueObject distance;
    private final TextValueObject duration;
    private final TextValueObject duration_in_traffic;

    public DirectionsLeg(final String end_address, final LatLng end_location, final String start_address,
                         final LatLng start_location, final List<DirectionsStep> steps,
                         final TextValueObject distance, final TextValueObject duration,
                         final TextValueObject duration_in_traffic) {
        this.end_address = end_address;
        this.end_location = end_location;
        this.start_address = start_address;
        this.start_location = start_location;
        this.steps = steps;
        this.distance = distance;
        this.duration = duration;
        this.duration_in_traffic = duration_in_traffic;
    }

    public String getEnd_address() {
        return this.end_address;
    }

    public LatLng getEnd_location() {
        return this.end_location;
    }

    public String getStart_address() {
        return this.start_address;
    }

    public LatLng getStart_location() {
        return this.start_location;
    }

    public List<DirectionsStep> getSteps() {
        return List.copyOf(this.steps);
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
