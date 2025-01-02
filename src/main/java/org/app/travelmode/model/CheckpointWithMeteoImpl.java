package org.app.travelmode.model;

import org.app.travelmode.directions.LatLng;

import java.time.ZonedDateTime;

public class CheckpointWithMeteoImpl extends CheckpointImpl implements CheckpointWithMeteo {

    public CheckpointWithMeteoImpl(double latitude, double longitude, final ZonedDateTime arrivalDateTime) {
        super(latitude, longitude, arrivalDateTime);
    }

}
