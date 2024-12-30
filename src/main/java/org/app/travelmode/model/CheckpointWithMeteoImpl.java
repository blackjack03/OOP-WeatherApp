package org.app.travelmode.model;

import org.app.travelmode.directions.LatLng;

import java.time.ZonedDateTime;

public class CheckpointWithMeteoImpl extends CheckpointImpl implements CheckpointWithMeteo {

    public CheckpointWithMeteoImpl(final LatLng coordinates, final ZonedDateTime arrivalDateTime) {
        super(coordinates, arrivalDateTime);
    }

}
