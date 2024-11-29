package org.app.travelmode.model;

public interface RouteAnalyzer {

    /**
     * Allows you to obtain a route from the departure and arrival locations specified in the travelRequest
     *
     * @param travelRequest contains all the elements necessary for calculating a trip between two places
     */
    void requestRoute(TravelRequest travelRequest);

}
