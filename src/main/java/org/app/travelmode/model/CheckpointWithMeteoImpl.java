package org.app.travelmode.model;

import java.time.ZonedDateTime;

public class CheckpointWithMeteoImpl extends CheckpointImpl implements CheckpointWithMeteo {

    private final WeatherReport weatherReport;

    public CheckpointWithMeteoImpl(double latitude, double longitude, final ZonedDateTime arrivalDateTime, final WeatherReport weatherReport) {
        super(latitude, longitude, arrivalDateTime);
        this.weatherReport = weatherReport;
    }

    public CheckpointWithMeteoImpl(final Checkpoint checkpoint, final WeatherReport weatherReport) {
        this(checkpoint.getLatitude(), checkpoint.getLongitude(), checkpoint.getArrivalDateTime(), weatherReport);
    }

    @Override
    public WeatherReport getWeatherReport() {
        return this.weatherReport;
    }

    @Override
    public int getWeatherScore() {
        return this.weatherReport.calculateWeatherScore();
    }
}
