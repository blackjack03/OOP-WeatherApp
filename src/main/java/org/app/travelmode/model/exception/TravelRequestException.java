package org.app.travelmode.model.exception;

/**
 * Custom exception class for handling errors related to travel requests.
 * This exception is thrown when there are issues with creating, validating,
 * or processing travel requests.
 */
public class TravelRequestException extends Exception {

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
