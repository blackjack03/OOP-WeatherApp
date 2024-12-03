package org.app.travelmode.model;

import java.time.LocalDate;
import java.time.LocalTime;

public final class TravelRequestImpl implements TravelRequest {

    private final String departureLocation;
    private final String departurePlaceId;
    private final String arrivalLocation;
    private final String arrivalPlaceId;
    private final LocalTime departureTime;
    private final LocalDate departureDate;

    private TravelRequestImpl(final String departureLocation, final String departurePlaceId, final String arrivalLocation, final String arrivalPlaceId, final LocalTime departureTime, final LocalDate departureDate) {
        this.departureLocation = departureLocation;
        this.departurePlaceId = departurePlaceId;
        this.arrivalLocation = arrivalLocation;
        this.arrivalPlaceId = arrivalPlaceId;
        this.departureTime = departureTime;
        this.departureDate = departureDate;
    }

    @Override
    public String getDepartureLocation() {
        return this.departureLocation;
    }

    @Override
    public String getDepartureLocationPlaceId() {
        return this.departurePlaceId;
    }

    @Override
    public String getArrivalLocation() {
        return this.arrivalLocation;
    }

    @Override
    public String getArrivalLocationPlaceId() {
        return this.arrivalPlaceId;
    }

    @Override
    public LocalTime getDepartureTime() {
        return this.departureTime;
    }

    @Override
    public LocalDate getDepartureDate() {
        return this.departureDate;
    }

    @Override
    public String toString() {
        return "{\n[Partenza: " + this.departureLocation + ", PlaceId:" + this.departurePlaceId +
                "\tOra: " + this.departureTime + ", Date: " + this.departureDate + "]\n" +
                "[Arrivo: " + this.arrivalLocation + ", PalceId: " + this.arrivalPlaceId + "]\n" +
                "}";
    }

    /**
     * Builder class of a TravelRequestImpl object
     */
    public static class Builder {

        private static final LocalTime DEPARTURE_TIME = LocalTime.now();
        private static final LocalDate DEPARTURE_DATE = LocalDate.now();

        private String departureLocation;
        private String departurePlaceId;
        private String arrivalLocation;
        private String arrivalPlaceId;
        private LocalTime departureTime = DEPARTURE_TIME;
        private LocalDate departureDate = DEPARTURE_DATE;

        /**
         * Set the departure location
         *
         * @param departureLocation the name of the departure location
         * @return this builder, for method chaining
         */
        public Builder setDepartureLocation(final String departureLocation) {
            this.departureLocation = departureLocation;
            return this;
        }

        /**
         * Set the PlaceId associated with the starting location
         *
         * @param departurePlaceId the PlaceId associated with the starting location
         * @return this builder, for method chaining
         */
        public Builder setDeparturePlaceId(final String departurePlaceId) {
            this.departurePlaceId = departurePlaceId;
            return this;
        }

        /**
         * Set the arrival location
         *
         * @param arrivalLocation the name of the arrival location
         * @return this builder, for method chaining
         */
        public Builder setArrivalLocation(final String arrivalLocation) {
            this.arrivalLocation = arrivalLocation;
            return this;
        }

        /**
         * Set the PlaceId associated with the arrival location
         *
         * @param arrivalPlaceId the PlaceId associated with the arrival location
         * @return this builder, for method chaining
         */
        public Builder setArrivalPlaceId(final String arrivalPlaceId) {
            this.arrivalPlaceId = arrivalPlaceId;
            return this;
        }

        /**
         * Set the departure time
         *
         * @param departureTime the departure time
         * @return this builder, for method chaining
         */
        public Builder setDepartureTime(final LocalTime departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        /**
         * Set the departure date
         *
         * @param departureDate the departure date
         * @return this builder, for method chaining
         */
        public Builder setDepartureDate(final LocalDate departureDate) {
            this.departureDate = departureDate;
            return this;
        }

        /**
         * If all the necessary parameters have been configured correctly a new TravelRequestImpl is returned.
         *
         * @return a new TravelRequestImpl.
         * @throws IllegalStateException If not all the necessary parameters have been entered
         */
        public final TravelRequestImpl build() throws IllegalStateException {
            if (!this.isReady()) {
                throw new IllegalStateException("Non sono stati inseriti tutti i parametri necessari per il calcolo del percorso");
            }
            return new TravelRequestImpl(departureLocation, departurePlaceId, arrivalLocation, arrivalPlaceId, departureTime, departureDate);
        }

        /**
         * Lets you know if the builder is ready to be used
         *
         * @return true if all necessary parameters have been configured correctly
         */
        public boolean isReady() {
            return departureLocation != null && !departureLocation.isBlank()
                    && departurePlaceId != null && !departurePlaceId.isBlank()
                    && arrivalLocation != null && !arrivalLocation.isBlank()
                    && arrivalPlaceId != null && !arrivalPlaceId.isBlank()
                    && departureTime != null && departureDate != null;
        }
    }
}
