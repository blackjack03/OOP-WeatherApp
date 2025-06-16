package org.app.travelmode.model.google.dto.directions;

import java.util.List;

public class DirectionsResponse {

    private List<DirectionsRoute> routes;
    private String status;

    public DirectionsResponse() {
    }

    public List<DirectionsRoute> getRoutes() {
        return this.routes;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "DirectionsResponse{" + "routes=\n" + routes + ",\n status=" + status + '}';
    }
}
