package org.app.travelmode.model.google.dto.directions;

import java.util.List;

/**
 * Represents a leg of a route in the Google Directions API response.
 * A leg represents a single section of the journey between two points,
 * containing detailed information about distance, duration, and navigation steps.
 *
 * <p>Each leg contains:
 * <ul>
 *     <li>Start and end addresses with their geographical coordinates</li>
 *     <li>A sequence of detailed navigation steps</li>
 *     <li>Total distance and duration information</li>
 *     <li>Optional real-time traffic duration data</li>
 * </ul>
 */
public class DirectionsLeg {

    private final String end_address;
    private final LatLng end_location;
    private final String start_address;
    private final LatLng start_location;
    private final List<DirectionsStep> steps;
    private final TextValueObject distance;
    private final TextValueObject duration;
    private final TextValueObject duration_in_traffic;

    /**
     * Constructs a new DirectionsLeg with all required information.
     *
     * @param end_address         the human-readable destination address
     * @param end_location        the geographical coordinates of the destination
     * @param start_address       the human-readable starting address
     * @param start_location      the geographical coordinates of the starting point
     * @param steps               the list of navigation steps for this leg
     * @param distance            the total distance of this leg
     * @param duration            the estimated duration without traffic
     * @param duration_in_traffic the estimated duration considering traffic
     */
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

    /**
     * Returns the human-readable address of the destination.
     *
     * @return the destination address as a string.
     */
    public String getEnd_address() {
        return this.end_address;
    }

    /**
     * Returns the geographical coordinates of the destination.
     *
     * @return the destination location coordinates.
     */
    public LatLng getEnd_location() {
        return this.end_location;
    }

    /**
     * Returns the human-readable address of the starting point.
     *
     * @return the starting address as a string.
     */
    public String getStart_address() {
        return this.start_address;
    }

    /**
     * Returns the geographical coordinates of the starting point.
     *
     * @return the starting location coordinates
     */
    public LatLng getStart_location() {
        return this.start_location;
    }

    /**
     * Returns an immutable list of navigation steps for this leg.
     *
     * @return a list of {@link DirectionsStep} objects representing the navigation steps
     */
    public List<DirectionsStep> getSteps() {
        return List.copyOf(this.steps);
    }

    /**
     * Returns the total distance of this leg.
     *
     * @return the distance information
     */
    public TextValueObject getDistance() {
        return this.distance;
    }

    /**
     * Returns the estimated duration without traffic considerations.
     *
     * @return the duration information
     */
    public TextValueObject getDuration() {
        return this.duration;
    }

    /**
     * Returns the estimated duration considering current traffic conditions.
     *
     * @return the traffic-aware duration information
     */
    public TextValueObject getDuration_in_traffic() {
        return this.duration_in_traffic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{DirectionsLeg [end_address=" + end_address + ",\n end_location=" + end_location +
                ",\n start_address=" + start_address + ",\n start_location=" + start_location +
                ",\n distance=" + distance + ",\n duration=" + duration + ",\n duration_in_traffic=" + duration_in_traffic +
                ",\n steps=" + steps + "\n}";
    }
}
