package org.app.travelmode.model.exception;

import java.io.Serial;

/**
 * Custom exception class for handling errors related to travel requests.
 * This exception is thrown when there are issues with creating, validating,
 * or processing travel requests.
 */
public class TravelRequestException extends Exception {

    @Serial
    private static final long serialVersionUID = -8157761256127862358L;

    /**
     * Constructs a new travel request exception with the specified detail message.
     *
     * @param message the detail message describing the error condition
     */
    public TravelRequestException(final String message) {
        super(message);
    }

    /**
     * Constructs a new travel request exception with the specified detail message and cause.
     *
     * @param message the detail message describing the error condition
     * @param cause   the cause of this exception
     */
    public TravelRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
