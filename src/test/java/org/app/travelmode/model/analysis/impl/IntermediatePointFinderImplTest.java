package org.app.travelmode.model.analysis.impl;

import com.google.gson.Gson;
import org.app.travelmode.model.analysis.api.SubStepGenerator;
import org.app.travelmode.model.google.dto.directions.DirectionsLeg;
import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.google.dto.directions.LatLng;
import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link IntermediatePointFinderImpl}, which computes intermediate steps
 * from a {@link DirectionsLeg} using a {@link SubStepGenerator}.
 * <p>
 * This test class verifies:
 * <ul>
 *   <li>Correct generation of multiple intermediate steps</li>
 *   <li>Correct handling of a single short or long step</li>
 *   <li>Correct content of each step (duration, distance, coordinates)</li>
 *   <li>That the returned list is immutable</li>
 * </ul>
 *
 * <p>Test data is loaded from static JSON files that simulate real responses
 * from the Google Directions API.</p>
 */
public class IntermediatePointFinderImplTest {

    private static final double DURATION_TOLERANCE_SECONDS = 60.0;
    private static final double DISTANCE_TOLERANCE_METERS = 500.0;

    private static final int EXPECTED_STEPS_FULL_ROUTE = 3;
    private static final int EXPECTED_STEPS_LONG_ROUTE = 3;

    private static final double STEP1_DURATION_FULL = 1468.3;
    private static final double STEP1_DISTANCE_FULL = 28735;
    private static final double STEP2_DURATION_FULL = 930;
    private static final double STEP2_DISTANCE_FULL = 28180;
    private static final double STEP3_DURATION_FULL = 1754;
    private static final double STEP3_DISTANCE_FULL = 21385;

    private static final double SINGLE_STEP_DURATION = 44;
    private static final double SINGLE_STEP_DISTANCE = 172;

    private static final double STEP1_DURATION_LONG = 935.8;
    private static final double STEP1_DISTANCE_LONG = 28216.1;
    private static final double STEP2_DURATION_LONG = 929.3;
    private static final double STEP2_DISTANCE_LONG = 28017.7;
    private static final double STEP3_DURATION_LONG = 80.9;
    private static final double STEP3_DISTANCE_LONG = 2439.2;

    private IntermediatePointFinderImpl finder;
    private SubStepGenerator subStepGenerator;

    /**
     * Initializes the test environment before each test case.
     */
    @BeforeEach
    void setUp() {
        finder = new IntermediatePointFinderImpl();
        subStepGenerator = new SubStepGeneratorImpl();
    }

    /**
     * Loads a json file containing the output of a call to the Google Directions API
     * and extracts the first DirectionsLeg from it.
     *
     * @param filePath The path of the json file.
     * @return the first {@link DirectionsLeg} of the first route
     * @throws IOException if an I/O error occurs while reading the json file.
     */
    private DirectionsLeg loadDirectionsLegFromJson(final String filePath) throws IOException {
        final String json = new String(Files.readAllBytes(Paths.get(filePath)));
        final Gson gson = new Gson();
        final DirectionsResponse directionsResponse = gson.fromJson(json, DirectionsResponse.class);

        return directionsResponse.getRoutes().get(0).getLegs().get(0);
    }

    /**
     * Asserts that the given {@link SimpleDirectionsStep} has expected values
     * for duration, distance, and coordinates.
     *
     * @param step             the step to verify
     * @param expectedDuration expected duration in seconds
     * @param expectedEnd      expected end location
     * @param expectedStart    expected start location
     * @param expectedDistance expected distance in meters
     * @param message          message used for assertion failure details
     */
    private void assertStep(final SimpleDirectionsStep step, final double expectedDuration, final LatLng expectedEnd,
                            final LatLng expectedStart, final double expectedDistance, final String message) {

        assertEquals(expectedDuration, step.getDuration().getValue(), DURATION_TOLERANCE_SECONDS,
                message + " - Durata non corretta");
        assertEquals(expectedEnd.getLat(), step.getEndLocation().getLat(),
                message + " - Latitudine del punto di arrivo non corretta");
        assertEquals(expectedEnd.getLng(), step.getEndLocation().getLng(),
                message + " - Longitudine del punto di arrivo non corretta");
        assertEquals(expectedStart.getLat(), step.getStartLocation().getLat(),
                message + " - Latitudine del punto di partenza non corretta");
        assertEquals(expectedStart.getLng(), step.getStartLocation().getLng(),
                message + " - Longitudine del punto di partenza non corretta");
        assertEquals(expectedDistance, step.getDistance().getValue(), DISTANCE_TOLERANCE_METERS,
                message + " - Distanza non corretta");
    }

    /**
     * Tests that intermediate points are correctly generated from a full route.
     */
    @Test
    void testFindIntermediatePointsWithValidJson() throws IOException {
        final LatLng forli = new LatLng(44.222008, 12.0408666);
        final LatLng p1 = new LatLng(44.34260, 11.84725);
        final LatLng p2 = new LatLng(44.45460, 11.53128);
        final LatLng bologna = new LatLng(44.4949026, 11.3426237);

        final DirectionsLeg leg = this.loadDirectionsLegFromJson("src/test/resources/directions_valid_response.json");

        final List<SimpleDirectionsStep> result = finder.findIntermediatePoints(leg, subStepGenerator);

        assertEquals(EXPECTED_STEPS_FULL_ROUTE, result.size(),
                "Dovrebbero essere generati 3 step");

        assertStep(result.get(0), STEP1_DURATION_FULL, p1, forli, STEP1_DISTANCE_FULL,
                "Primo step");
        assertStep(result.get(1), STEP2_DURATION_FULL, p2, p1, STEP2_DISTANCE_FULL,
                "Secondo step");
        assertStep(result.get(2), STEP3_DURATION_FULL, bologna, p2, STEP3_DISTANCE_FULL,
                "Terzo step");
    }

    /**
     * Tests that a single step is generated when the route consists of a short path.
     */
    @Test
    void testFindIntermediatePointsWithOneSteps() throws IOException {
        final LatLng start = new LatLng(44.14727329999999, 12.2366031);
        final LatLng end = new LatLng(44.1459731, 12.2377608);

        final DirectionsLeg leg = this.loadDirectionsLegFromJson("src/test/resources/directions_one_step_response.json");

        final List<SimpleDirectionsStep> result = finder.findIntermediatePoints(leg, subStepGenerator);

        assertEquals(1, result.size(),
                "Dovrebbe essere generato un solo step");

        assertStep(result.get(0), SINGLE_STEP_DURATION, end, start, SINGLE_STEP_DISTANCE,
                "Singolo step");
    }

    /**
     * Tests that a long single step is properly split into multiple intermediate steps.
     */
    @Test
    void testFindIntermediatePointsWithOneLongSteps() throws IOException {
        final LatLng start = new LatLng(44.2519312, 12.0918587);
        final LatLng p1 = new LatLng(44.36817, 11.77675);
        final LatLng p2 = new LatLng(44.47588, 11.46013);
        final LatLng end = new LatLng(44.480675, 11.4329521);

        final DirectionsLeg leg = this.loadDirectionsLegFromJson("src/test/resources/directions_one_long_step_response.json");

        final List<SimpleDirectionsStep> result = finder.findIntermediatePoints(leg, subStepGenerator);

        assertEquals(EXPECTED_STEPS_LONG_ROUTE, result.size(),
                "Dovrebbero essere generati 3 step");

        assertStep(result.get(0), STEP1_DURATION_LONG, p1, start, STEP1_DISTANCE_LONG,
                "Primo step");
        assertStep(result.get(1), STEP2_DURATION_LONG, p2, p1, STEP2_DISTANCE_LONG,
                "Secondo step");
        assertStep(result.get(2), STEP3_DURATION_LONG, end, p2, STEP3_DISTANCE_LONG,
                "Terzo step");
    }

    /**
     * Tests that the result list returned by {@code findIntermediatePoints} is immutable.
     */
    @Test
    void testFindIntermediatePointsReturnsImmutableList() throws IOException {
        final LatLng p1 = new LatLng(12.34, 43.21);
        final LatLng p2 = new LatLng(56.78, 87.65);

        final DirectionsLeg leg = this.loadDirectionsLegFromJson("src/test/resources/directions_valid_response.json");

        final List<SimpleDirectionsStep> result = finder.findIntermediatePoints(leg, subStepGenerator);

        assertThrows(UnsupportedOperationException.class,
                () -> result.add(new SimpleDirectionsStep(0, p1, p2, 0)),
                "La lista risultante dovrebbe essere immutabile");
    }

}
