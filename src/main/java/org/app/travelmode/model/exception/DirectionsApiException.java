package org.app.travelmode.model.exception;

import java.io.Serial;

/**
 * Exception thrown when errors occur during interactions with the Google Directions API.
 *
 * <p>The exception provides two constructors:
 * <ul>
 *     <li>One for simple error messages</li>
 *     <li>Another for wrapping underlying exceptions with additional context</li>
 * </ul>
 */
public class DirectionsApiException extends Exception {

    @Serial
    private static final long serialVersionUID = -8221141337115352220L;

    /**
     * Constructs a new directions API exception with the specified detail message.
     *
     * @param message the detail message describing the error condition
     */
    public DirectionsApiException(final String message) {
        super(message);
    }

    /**
     * Constructs a new directions API exception with the specified detail message and cause.
     *
     * @param message the detail message describing the error condition
     * @param cause   the cause of this exception
     */
    public DirectionsApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
