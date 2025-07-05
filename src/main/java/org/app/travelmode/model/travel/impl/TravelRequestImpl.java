package org.app.travelmode.model.travel.impl;

import org.app.travelmode.model.exception.TravelRequestException;
import org.app.travelmode.model.travel.api.TravelRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Implementation of the {@link TravelRequest} interface.
 * <p>
 * It represents a request with all the elements necessary to calculate a trip between two places.
 */
public final class TravelRequestImpl implements TravelRequest {

    private final String departureLocation;
    private final String departurePlaceId;
    private final String arrivalLocation;
    private final String arrivalPlaceId;
    private final LocalTime departureTime;
    private final LocalDate departureDate;
    private final ZoneId departureTimeZone;
    private final ZonedDateTime departureDateTime;

    /**
     * Constructs a new {@link TravelRequestImpl} object with the specified parameters.
     *
     * @param departureLocation the name of the departure location.
     * @param departurePlaceId  the PlaceId associated with the departure location.
     * @param arrivalLocation   the name of the arrival location.
     * @param arrivalPlaceId    the PlaceId associated with the arrival location.
     * @param departureTime     the departure time.
     * @param departureDate     the departure date.
     * @param departureTimeZone the time zone of the departure location.
     */
    private TravelRequestImpl(final String departureLocation, final String departurePlaceId,
                              final String arrivalLocation, final String arrivalPlaceId, final LocalTime departureTime,
                              final LocalDate departureDate, final ZoneId departureTimeZone) {
        this.departureLocation = departureLocation;
        this.departurePlaceId = departurePlaceId;
        this.arrivalLocation = arrivalLocation;
        this.arrivalPlaceId = arrivalPlaceId;
        this.departureTime = departureTime;
        this.departureDate = departureDate;
        this.departureTimeZone = departureTimeZone;
        this.departureDateTime = ZonedDateTime.of(departureDate, departureTime, departureTimeZone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDepartureLocation() {
        return this.departureLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDepartureLocationPlaceId() {
        return this.departurePlaceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArrivalLocation() {
        return this.arrivalLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArrivalLocationPlaceId() {
        return this.arrivalPlaceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalTime getDepartureTime() {
        return this.departureTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate getDepartureDate() {
        return this.departureDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZoneId getDepartureTimeZone() {
        return this.departureTimeZone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime getDepartureDateTime() {
        return this.departureDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{\n[Partenza: " + this.departureLocation + ", PlaceId:" + this.departurePlaceId
                + "\tOra: " + this.departureTime + ", Date: " + this.departureDate + "]\n"
                + "[Arrivo: " + this.arrivalLocation + ", PalceId: " + this.arrivalPlaceId + "]\n" + "}";
    }

    /**
     * Builder class of a TravelRequestImpl object.
     */
    public static class Builder {

        private static final LocalTime DEPARTURE_TIME = LocalTime.now();
        private static final LocalDate DEPARTURE_DATE = LocalDate.now();
        private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
        private static final String INCOMPLETE_REQUEST =
                "Non sono stati inseriti tutti i parametri necessari per il calcolo del percorso";

        private String departureLocation;
        private String departurePlaceId;
        private String arrivalLocation;
        private String arrivalPlaceId;
        private LocalTime departureTime = DEPARTURE_TIME;
        private LocalDate departureDate = DEPARTURE_DATE;
        private ZoneId departureZoneId = DEFAULT_ZONE_ID;

        /**
         * Set the departure location.
         *
         * @param departureLocation the name of the departure location
         * @return this builder, for method chaining
         */
        public Builder addDepartureLocation(final String departureLocation) {
            this.departureLocation = departureLocation;
            return this;
        }

        /**
         * Set the PlaceId associated with the starting location.
         *
         * @param departurePlaceId the PlaceId associated with the starting location
         * @return this builder, for method chaining
         */
        public Builder addDeparturePlaceId(final String departurePlaceId) {
            this.departurePlaceId = departurePlaceId;
            return this;
        }

        /**
         * Set the arrival location.
         *
         * @param arrivalLocation the name of the arrival location
         * @return this builder, for method chaining
         */
        public Builder addArrivalLocation(final String arrivalLocation) {
            this.arrivalLocation = arrivalLocation;
            return this;
        }

        /**
         * Set the PlaceId associated with the arrival location.
         *
         * @param arrivalPlaceId the PlaceId associated with the arrival location
         * @return this builder, for method chaining
         */
        public Builder addArrivalPlaceId(final String arrivalPlaceId) {
            this.arrivalPlaceId = arrivalPlaceId;
            return this;
        }

        /**
         * Set the departure time.
         *
         * @param departureTime the departure time
         * @return this builder, for method chaining
         */
        public Builder addDepartureTime(final LocalTime departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        /**
         * Set the departure date.
         *
         * @param departureDate the departure date
         * @return this builder, for method chaining
         */
        public Builder addDepartureDate(final LocalDate departureDate) {
            this.departureDate = departureDate;
            return this;
        }

        /**
         * Set the {@link ZoneId} of the departure location.
         *
         * @param departureZoneId the {@link ZoneId} of the departure location
         * @return this builder, for method chaining
         */
        public Builder addDepartureZoneId(final ZoneId departureZoneId) {
            this.departureZoneId = departureZoneId;
            return this;
        }

        /**
         * If all the necessary parameters have been configured correctly a new TravelRequestImpl is returned.
         *
         * @return a new TravelRequestImpl.
         * @throws TravelRequestException If not all the necessary parameters have been entered
         */
        public final TravelRequestImpl build() throws TravelRequestException {
            if (!this.isReady()) {
                throw new TravelRequestException(INCOMPLETE_REQUEST);
            }
            return new TravelRequestImpl(departureLocation, departurePlaceId,
                    arrivalLocation, arrivalPlaceId, departureTime, departureDate, departureZoneId);
        }

        /**
         * Lets you know if the builder is ready to be used.
         *
         * @return true if all necessary parameters have been configured correctly
         */
        public boolean isReady() {
            return departureLocation != null && !departureLocation.isBlank()
                    && departurePlaceId != null && !departurePlaceId.isBlank()
                    && arrivalLocation != null && !arrivalLocation.isBlank()
                    && arrivalPlaceId != null && !arrivalPlaceId.isBlank()
                    && departureTime != null && departureDate != null && departureZoneId != null;
        }
    }
}
