package org.app.travelmode.model.google.dto.directions;

import java.util.List;
import java.util.Objects;

public class DirectionsResponse {

    private final List<DirectionsRoute> routes;
    private final String status;
    private final String error_message;

    public DirectionsResponse(final String status, final List<DirectionsRoute> routes, final String errorMessage) {
        this.status = status;
        this.routes = routes;
        this.error_message = errorMessage;
    }

    public DirectionsResponse(final String status, final List<DirectionsRoute> routes) {
        this(status, routes, null);
    }

    public List<DirectionsRoute> getRoutes() {
        return this.routes;
    }

    public String getStatus() {
        return this.status;
    }

    public boolean hasAlternatives() {
        return this.routes.size() > 1;
    }

    public String getErrorMessage() {
        return Objects.requireNonNullElse(this.error_message, "");
    }

    @Override
    public String toString() {
        return "DirectionsResponse{" + "routes=\n" + routes + ",\n status=" + status + '}';
    }
}
