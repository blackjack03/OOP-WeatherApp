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
@SuppressWarnings("PMD.FieldNamingConventions")
public class DirectionsLeg {

    // CHECKSTYLE: MemberName OFF
    // Fields used by Gson: names must match exactly the received JSON
    private final String end_address;
    private final LatLng end_location;
    private final String start_address;
    private final LatLng start_location;
    private final List<DirectionsStep> steps;
    private final TextValueObject distance;
    private final TextValueObject duration;
    private final TextValueObject duration_in_traffic;
    // CHECKSTYLE: MemberName ON

    /**
     * Constructs a new DirectionsLeg with all required information.
     *
     * @param endAddress        the human-readable destination address
     * @param endLocation       the geographical coordinates of the destination
     * @param startAddress      the human-readable starting address
     * @param startLocation     the geographical coordinates of the starting point
     * @param steps             the list of navigation steps for this leg
     * @param distance          the total distance of this leg
     * @param duration          the estimated duration without traffic
     * @param durationInTraffic the estimated duration considering traffic
     */
    public DirectionsLeg(final String endAddress, final LatLng endLocation, final String startAddress,
                         final LatLng startLocation, final List<DirectionsStep> steps,
                         final TextValueObject distance, final TextValueObject duration,
                         final TextValueObject durationInTraffic) {
        this.end_address = endAddress;
        this.end_location = endLocation;
        this.start_address = startAddress;
        this.start_location = startLocation;
        this.steps = List.copyOf(steps);
        this.distance = distance;
        this.duration = duration;
        this.duration_in_traffic = durationInTraffic;
    }

    /**
     * Returns the human-readable address of the destination.
     *
     * @return the destination address as a string.
     */
    public String getEndAddress() {
        return this.end_address;
    }

    /**
     * Returns the geographical coordinates of the destination.
     *
     * @return the destination location coordinates.
     */
    public LatLng getEndLocation() {
        return this.end_location;
    }

    /**
     * Returns the human-readable address of the starting point.
     *
     * @return the starting address as a string.
     */
    public String getStartAddress() {
        return this.start_address;
    }

    /**
     * Returns the geographical coordinates of the starting point.
     *
     * @return the starting location coordinates
     */
    public LatLng getStartLocation() {
        return this.start_location;
    }

    /**
     * Returns an immutable list of navigation steps for this leg.
     *
     * @return a list of {@link DirectionsStep} objects representing the navigation steps
     */
    public List<DirectionsStep> getSteps() {
        return this.steps;
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
    public TextValueObject getDurationInTraffic() {
        return this.duration_in_traffic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{DirectionsLeg [end_address=" + end_address + ",\n end_location=" + end_location
                + ",\n start_address=" + start_address + ",\n start_location=" + start_location
                + ",\n distance=" + distance + ",\n duration=" + duration + ",\n duration_in_traffic="
                + duration_in_traffic + ",\n steps=" + steps + "\n}";
    }
}
