package org.app.travelmode.model.google.dto.directions;

import java.util.List;
import java.util.Objects;

/**
 * Represents a response received from the Google Directions API.
 * This class encapsulates data related to calculated routes between two points,
 * including alternative routes if available.
 *
 * <p>The response contains:
 * <ul>
 *     <li>A list of possible routes</li>
 *     <li>The request status</li>
 *     <li>Any error messages</li>
 * </ul>
 */
@SuppressWarnings("PMD.FieldNamingConventions")
public class DirectionsResponse {

    // CHECKSTYLE: MemberName OFF
    // Fields used by Gson: names must match exactly the received JSON
    private final List<DirectionsRoute> routes;
    private final String status;
    private final String error_message;
    // CHECKSTYLE: MemberName ON

    /**
     * Constructs a new response with status, routes, and error message.
     *
     * @param status       status of the API response
     * @param routes       list of calculated routes
     * @param errorMessage optional error message
     */
    public DirectionsResponse(final String status, final List<DirectionsRoute> routes, final String errorMessage) {
        this.status = status;
        this.routes = routes;
        this.error_message = errorMessage;
    }

    /**
     * Constructs a new response with status and routes.
     *
     * @param status status of the API response
     * @param routes list of calculated routes
     */
    public DirectionsResponse(final String status, final List<DirectionsRoute> routes) {
        this(status, routes, null);
    }

    /**
     * Returns the list of calculated routes.
     *
     * @return list of available routes
     */
    public List<DirectionsRoute> getRoutes() {
        return List.copyOf(this.routes);
    }

    /**
     * Returns the response status.
     *
     * @return the API response status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Checks if alternative routes are available.
     *
     * @return true if multiple alternative routes are present,
     * false if only the main route is available
     */
    public boolean hasAlternatives() {
        return this.routes.size() > 1;
    }

    /**
     * Returns the error message, if present.
     *
     * @return the error message or an empty string if not present
     */
    public String getErrorMessage() {
        return Objects.requireNonNullElse(this.error_message, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DirectionsResponse{" + "routes=\n" + routes + ",\n status=" + status + '}';
    }
}
