package org.app.travelmode.model.checkpoint.impl;

import org.app.travelmode.model.checkpoint.api.Checkpoint;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;
import org.app.travelmode.model.weather.api.WeatherReport;
import org.app.travelmode.model.weather.impl.WeatherReportImpl;

import java.time.ZonedDateTime;

/**
 * Implementation of {@link CheckpointWithMeteo} that extends {@link CheckpointImpl}
 * to include weather information at the checkpoint location.
 *
 * <p>This class enhances the basic checkpoint functionality by adding:
 * <ul>
 *     <li>Weather condition reports</li>
 *     <li>Weather score calculations</li>
 * </ul>
 */
public class CheckpointWithMeteoImpl extends CheckpointImpl implements CheckpointWithMeteo {

    private final WeatherReport weatherReport;

    /**
     * Constructs a new checkpoint with weather information using explicit parameters.
     *
     * @param latitude        the latitude in decimal degrees (-90° to +90°)
     * @param longitude       the longitude in decimal degrees (-180° to +180°)
     * @param arrivalDateTime the expected arrival time at this checkpoint
     * @param weatherReport   the weather conditions report for this location and time
     */
    public CheckpointWithMeteoImpl(final double latitude, final double longitude, final ZonedDateTime arrivalDateTime,
                                   final WeatherReport weatherReport) {
        super(latitude, longitude, arrivalDateTime);
        this.weatherReport = weatherReport;
    }

    /**
     * Constructs a new checkpoint with weather information from an existing checkpoint.
     *
     * <p>This constructor allows conversion of a regular checkpoint into one with
     * weather information while preserving all original checkpoint data.
     *
     * @param checkpoint    the existing checkpoint to enhance with weather information
     * @param weatherReport the weather conditions report to associate with the checkpoint
     */
    public CheckpointWithMeteoImpl(final Checkpoint checkpoint, final WeatherReport weatherReport) {
        this(checkpoint.getLatitude(), checkpoint.getLongitude(), checkpoint.getArrivalDateTime(), weatherReport);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WeatherReport getWeatherReport() {
        return new WeatherReportImpl(this.weatherReport);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWeatherScore() {
        return this.weatherReport.getWeatherScore();
    }
}
