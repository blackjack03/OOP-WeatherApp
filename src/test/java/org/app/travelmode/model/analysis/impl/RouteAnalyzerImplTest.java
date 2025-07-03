package org.app.travelmode.model.analysis.impl;

import com.google.gson.Gson;
import org.app.travelmode.model.analysis.api.IntermediatePointFinder;
import org.app.travelmode.model.analysis.api.SubStepGenerator;
import org.app.travelmode.model.google.dto.directions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteAnalyzerImplTest {

    private DirectionsRoute directionsRoute;
    private RouteAnalyzerImpl routeAnalyzer;

    @BeforeEach
    void setUp() throws IOException {
        final String json = new String(Files.readAllBytes(Paths.get("src/test/resources/fake_directions_route.json")));
        final Gson gson = new Gson();
        this.directionsRoute = gson.fromJson(json, DirectionsRoute.class);

        final SubStepGenerator fakeSubStepGenerator = (final DirectionsStep step) -> List.of();

        final IntermediatePointFinder fakePointFinder = (leg, generator) -> {
            final List<SimpleDirectionsStep> result = new ArrayList<>();
            for (final DirectionsStep step : leg.getSteps()) {
                final TextValueObject duration = step.getDuration();
                final LatLng start = step.getStartLocation();
                final LatLng end = step.getEndLocation();
                final String polylineName = step.getPolyline().getPoints();
                final TextValueObject distance = new TextValueObject(polylineName, step.getDistance().getValue());

                result.add(new SimpleDirectionsStep(duration, end, start, distance));
            }
            return result;
        };

        routeAnalyzer = new RouteAnalyzerImpl(fakePointFinder, fakeSubStepGenerator);
    }

    @Test
    void testCalculateIntermediatePointsPreservesOrder() {
        final List<SimpleDirectionsStep> result = routeAnalyzer.calculateIntermediatePoints(directionsRoute);

        final List<String> expectedPolylineNames = List.of(
                "leg1step1", "leg1step2",
                "leg2step1", "leg2step2", "leg2step3",
                "leg3step1", "leg3step2"
        );

        final List<String> actualPolylineNames = result.stream()
                .map(step -> step.getDistance().getText())
                .toList();

        assertEquals(7, result.size(), "Il numero totale di SimpleDirectionsStep dovrebbe essere 7");
        assertEquals(expectedPolylineNames, actualPolylineNames, "L'ordine dei SimpleDirectionsStep non è mantenuto");
    }

    @Test
    void testConsumedFlagPreventsReuse() {
        routeAnalyzer.calculateIntermediatePoints(directionsRoute);

        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> routeAnalyzer.calculateIntermediatePoints(directionsRoute));

        assertEquals("Questo RouteAnalyzer è già stato consumato", exception.getMessage());
    }
}