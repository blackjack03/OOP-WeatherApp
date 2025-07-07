package org.app.travelmode.model.analysis.impl;

import org.app.travelmode.model.checkpoint.api.Checkpoint;
import org.app.travelmode.model.checkpoint.impl.CheckpointImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.app.travelmode.model.google.dto.directions.LatLng;
import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link CheckpointGeneratorImpl} class.
 * <p>
 * This test suite verifies the correct generation of checkpoints from a list of {@link SimpleDirectionsStep},
 * based on a given departure time.
 * </p>
 *
 * <p>Tested behaviors include:</p>
 * <ul>
 *   <li>Correct number and content of checkpoints for single and multiple steps</li>
 *   <li>Accurate time calculations based on cumulative durations</li>
 *   <li>Immutability of the returned list</li>
 *   <li>Correct handling of steps with zero duration</li>
 * </ul>
 */
class CheckpointGeneratorImplTest {

    private static final double SINGLE_DURATION_SEC = 600.0;  // 10 minutes
    private static final double STEP1_DURATION_SEC = 1800.0;  // 30 minutes
    private static final double STEP2_DURATION_SEC = 1200.0;  // 20 minutes
    private static final double ZERO_DURATION_SEC = 0.0;

    private static final double SINGLE_DISTANCE_METRES = 1500.0;
    private static final double STEP1_DISTANCE_METRES = 25_000.0;
    private static final double STEP2_DISTANCE_METRES = 15_000.0;
    private static final double ZERO_STEP_DISTANCE_M = 100.0;

    private static final double LAT_ZERO = 0.0;
    private static final double LNG_ZERO = 0.0;


    private CheckpointGeneratorImpl generator;
    private ZonedDateTime departureTime;

    /**
     * Initializes the checkpoint generator and sets the departure time before each test.
     */
    @BeforeEach
    void setUp() {
        generator = new CheckpointGeneratorImpl();
        departureTime = ZonedDateTime.now();
    }

    /**
     * Utility method that checks the coordinates and arrival time of a generated checkpoint
     * against expected values.
     *
     * @param expectedLat  the expected latitude
     * @param expectedLng  the expected longitude
     * @param expectedTime the expected arrival time
     * @param checkpoint   the generated checkpoint to validate
     * @param locationName a label used in assertion messages
     */
    private void assertCheckpoint(final double expectedLat, final double expectedLng, final ZonedDateTime expectedTime,
                                  final Checkpoint checkpoint, final String locationName) {
        assertEquals(expectedLat, checkpoint.getLatitude(),
                String.format("La latitudine del checkpoint di %s non corrisponde", locationName));
        assertEquals(expectedLng, checkpoint.getLongitude(),
                String.format("La longitudine del checkpoint di %s non corrisponde", locationName));
        assertEquals(expectedTime, checkpoint.getArrivalDateTime(),
                String.format("L'orario di arrivo a %s non è corretto", locationName));
    }

    /**
     * Verifies that a single-step route generates exactly two checkpoints:
     * the start and the end.
     */
    @Test
    void testGenerateCheckpointsWithSingleStep() {
        final LatLng start = new LatLng(41.9028, 12.4964);
        final LatLng end = new LatLng(41.9109, 12.4818);

        final SimpleDirectionsStep step = new SimpleDirectionsStep(SINGLE_DURATION_SEC, end, start, SINGLE_DISTANCE_METRES);
        final List<SimpleDirectionsStep> steps = List.of(step);

        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertEquals(2, checkpoints.size(),
                "Il percorso con un singolo step dovrebbe generare 2 checkpoint (inizio e fine)");

        this.assertCheckpoint(start.getLat(), start.getLng(), departureTime,
                checkpoints.get(0), "punto di partenza");

        this.assertCheckpoint(end.getLat(), end.getLng(), departureTime.plusSeconds((long) SINGLE_DURATION_SEC),
                checkpoints.get(1), "punto di arrivo");
    }

    /**
     * Verifies that a multi-step route generates the correct number of checkpoints
     * and that arrival times accumulate correctly across steps.
     */
    @Test
    void testGenerateCheckpointsWithMultipleSteps() {
        // Prima tappa: Roma -> Tivoli -> Frascati
        final LatLng roma = new LatLng(41.9028, 12.4964);
        final LatLng tivoli = new LatLng(41.9633, 12.7958);
        final LatLng frascati = new LatLng(41.8089, 12.6799);

        final SimpleDirectionsStep step1 = new SimpleDirectionsStep(STEP1_DURATION_SEC, tivoli, roma, STEP1_DISTANCE_METRES);
        final SimpleDirectionsStep step2 = new SimpleDirectionsStep(STEP2_DURATION_SEC, frascati, tivoli, STEP2_DISTANCE_METRES);

        final List<SimpleDirectionsStep> steps = List.of(step1, step2);
        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertEquals(3, checkpoints.size(),
                "Il percorso con due step dovrebbe generare 3 checkpoint");

        this.assertCheckpoint(roma.getLat(), roma.getLng(), departureTime, checkpoints.get(0), "Roma");

        this.assertCheckpoint(tivoli.getLat(), tivoli.getLng(), departureTime.plusSeconds((long) STEP1_DURATION_SEC),
                checkpoints.get(1), "Tivoli");

        this.assertCheckpoint(frascati.getLat(), frascati.getLng(),
                departureTime.plusSeconds((long) (STEP1_DURATION_SEC + STEP2_DURATION_SEC)),
                checkpoints.get(2), "Frascati");

    }

    /**
     * Verifies that the list of generated checkpoints is immutable.
     */
    @Test
    void testGenerateCheckpointsReturnsImmutableList() {
        final LatLng start = new LatLng(41.9028, 12.4964);
        final LatLng end = new LatLng(41.9109, 12.4818);

        final SimpleDirectionsStep step = new SimpleDirectionsStep(SINGLE_DURATION_SEC, end, start, SINGLE_DISTANCE_METRES);

        final List<SimpleDirectionsStep> steps = List.of(step);
        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertThrows(UnsupportedOperationException.class,
                () -> checkpoints.add(new CheckpointImpl(LAT_ZERO, LNG_ZERO, ZonedDateTime.now())),
                "La lista di checkpoint dovrebbe essere immutabile");
    }

    /**
     * Verifies that a step with zero duration results in two checkpoints with identical arrival times.
     */
    @Test
    void testGenerateCheckpointsWithZeroDuration() {
        final LatLng start = new LatLng(41.9028, 12.4964);
        final LatLng end = new LatLng(41.9109, 12.4818);

        final SimpleDirectionsStep step = new SimpleDirectionsStep(ZERO_DURATION_SEC, end, start, ZERO_STEP_DISTANCE_M);

        final List<SimpleDirectionsStep> steps = List.of(step);
        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertEquals(checkpoints.get(0).getArrivalDateTime(), checkpoints.get(1).getArrivalDateTime(),
                "Per uno step con durata zero, i tempi di arrivo dovrebbero essere identici");
    }
}
