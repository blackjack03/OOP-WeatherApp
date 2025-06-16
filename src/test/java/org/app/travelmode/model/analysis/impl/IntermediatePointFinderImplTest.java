package org.app.travelmode.model;

import com.google.gson.Gson;
import org.app.travelmode.model.analysis.IntermediatePointFinderImpl;
import org.app.travelmode.model.analysis.SubStepGenerator;
import org.app.travelmode.model.analysis.SubStepGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.app.travelmode.directions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class IntermediatePointFinderImplTest {
    private IntermediatePointFinderImpl finder;
    private SubStepGenerator subStepGenerator;

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
     * Utility method to check a SimpleDirectionsStep
     */
    private void assertStep(final SimpleDirectionsStep step, double expectedDuration, final LatLng expectedEnd,
                            final LatLng expectedStart, double expectedDistance, final String message) {

        assertEquals(expectedDuration, step.getDuration().getValue(), 60,
                message + " - Durata non corretta");
        assertEquals(expectedEnd.getLat(), step.getEnd_location().getLat(),
                message + " - Latitudine del punto di arrivo non corretta");
        assertEquals(expectedEnd.getLng(), step.getEnd_location().getLng(),
                message + " - Longitudine del punto di arrivo non corretta");
        assertEquals(expectedStart.getLat(), step.getStart_location().getLat(),
                message + " - Latitudine del punto di partenza non corretta");
        assertEquals(expectedStart.getLng(), step.getStart_location().getLng(),
                message + " - Longitudine del punto di partenza non corretta");
        assertEquals(expectedDistance, step.getDistance().getValue(), 500,
                message + " - Distanza non corretta");
    }

    @Test
    void testFindIntermediatePointsWithValidJson() throws IOException {
        final LatLng forli = new LatLng(44.222008, 12.0408666);
        final LatLng p1 = new LatLng(44.34260, 11.84725);
        final LatLng p2 = new LatLng(44.45460, 11.53128);
        final LatLng bologna = new LatLng(44.4949026, 11.3426237);

        final DirectionsLeg leg = this.loadDirectionsLegFromJson("src/test/resources/directions_valid_response.json");

        final List<SimpleDirectionsStep> result = finder.findIntermediatePoints(leg, subStepGenerator);

        assertEquals(3, result.size(),
                "Dovrebbero essere generati 3 step");

        assertStep(result.get(0), 1468.3, p1, forli, 28735,
                "Primo step");
        assertStep(result.get(1), 930, p2, p1, 28180,
                "Secondo step");
        assertStep(result.get(2), 1754, bologna, p2, 21385,
                "Terzo step");
    }

    @Test
    void testFindIntermediatePointsWithOneSteps() throws IOException {
        final LatLng start = new LatLng(44.14727329999999, 12.2366031);
        final LatLng end = new LatLng(44.1459731, 12.2377608);

        final DirectionsLeg leg = this.loadDirectionsLegFromJson("src/test/resources/directions_one_step_response.json");

        final List<SimpleDirectionsStep> result = finder.findIntermediatePoints(leg, subStepGenerator);

        assertEquals(1, result.size(),
                "Dovrebbe essere generato un solo step");

        assertStep(result.get(0), 44, end, start, 172,
                "Singolo step");
    }

    @Test
    void testFindIntermediatePointsWithOneLongSteps() throws IOException {
        final LatLng start = new LatLng(44.2519312, 12.0918587);
        final LatLng p1 = new LatLng(44.36817, 11.77675);
        final LatLng p2 = new LatLng(44.47588, 11.46013);
        final LatLng end = new LatLng(44.480675, 11.4329521);

        final DirectionsLeg leg = this.loadDirectionsLegFromJson("src/test/resources/directions_one_long_step_response.json");

        final List<SimpleDirectionsStep> result = finder.findIntermediatePoints(leg, subStepGenerator);

        assertEquals(3, result.size(),
                "Dovrebbero essere generati 3 step");

        assertStep(result.get(0), 935.8, p1, start, 28216.1,
                "Primo step");
        assertStep(result.get(1), 929.3, p2, p1, 28017.7,
                "Secondo step");
        assertStep(result.get(2), 80.9, end, p2, 2439.2,
                "Terzo step");
    }

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
