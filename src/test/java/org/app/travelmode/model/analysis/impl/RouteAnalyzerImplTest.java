package org.app.travelmode.model.analysis.impl;

import com.google.gson.Gson;
import org.app.travelmode.model.analysis.api.IntermediatePointFinder;
import org.app.travelmode.model.analysis.api.SubStepGenerator;

import org.app.travelmode.model.google.dto.directions.DirectionsRoute;
import org.app.travelmode.model.google.dto.directions.DirectionsStep;
import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;
import org.app.travelmode.model.google.dto.directions.TextValueObject;
import org.app.travelmode.model.google.dto.directions.LatLng;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link RouteAnalyzerImpl} class.
 * <p>
 * This test class verifies the behavior of route analysis logic, specifically
 * how {@link SimpleDirectionsStep} objects are derived from a {@link DirectionsRoute}.
 * </p>
 *
 * <p>Tests include:</p>
 * <ul>
 *   <li>Correct ordering and total count of generated steps</li>
 *   <li>One-time consumption enforcement of the analyzer instance</li>
 * </ul>
 *
 */
public class RouteAnalyzerImplTest {

    private static final int EXPECTED_STEP_NUMBER = 7;

    private DirectionsRoute directionsRoute;
    private RouteAnalyzerImpl routeAnalyzer;

    /**
     * Loads a fake {@link DirectionsRoute} from JSON and prepares a {@link RouteAnalyzerImpl}
     * with fake dependencies for controlled testing.
     *
     * @throws IOException if the test JSON file cannot be read
     */
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

    /**
     * Verifies that {@code calculateIntermediatePoints} returns a correctly ordered list of
     * {@link SimpleDirectionsStep} and preserves the structure of the input route.
     */
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

        assertEquals(EXPECTED_STEP_NUMBER, result.size(), "Il numero totale di SimpleDirectionsStep dovrebbe essere 7");
        assertEquals(expectedPolylineNames, actualPolylineNames, "L'ordine dei SimpleDirectionsStep non è mantenuto");
    }

    /**
     * Verifies that calling {@code calculateIntermediatePoints} more than once
     * throws an {@link IllegalStateException}, enforcing single-use behavior.
     */
    @Test
    void testConsumedFlagPreventsReuse() {
        routeAnalyzer.calculateIntermediatePoints(directionsRoute);

        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> routeAnalyzer.calculateIntermediatePoints(directionsRoute));

        assertEquals("Questo RouteAnalyzer è già stato consumato", exception.getMessage());
    }
}
