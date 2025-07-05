package org.app.travelmode.model.exception;

import java.io.Serial;

/**
 * Exception thrown when errors occur during weather data operations.
 */
public class WeatherDataException extends Exception {

    @Serial
    private static final long serialVersionUID = -2616452415141941011L;

    /**
     * Constructs a new weather data exception with the specified detail message.
     *
     * @param message the detail message describing the error condition
     */
    public WeatherDataException(final String message) {
        super(message);
    }


    /**
     * Constructs a new weather data exception with the specified detail message and cause.
     *
     * @param message the detail message describing the error condition
     * @param cause   the cause of this exception
     */
    public WeatherDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
