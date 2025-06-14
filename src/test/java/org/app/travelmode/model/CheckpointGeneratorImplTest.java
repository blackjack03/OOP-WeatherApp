package org.app.travelmode.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import org.app.travelmode.directions.LatLng;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.time.ZonedDateTime;
import java.util.List;


public class CheckpointGeneratorImplTest {

    private CheckpointGeneratorImpl generator;
    private ZonedDateTime departureTime;

    @BeforeEach
    void setUp() {
        generator = new CheckpointGeneratorImpl();
        departureTime = ZonedDateTime.now();
    }

    /**
     * Utility method to verify the correctness of a checkpoint
     *
     * @param expectedLat  expected latitude
     * @param expectedLng  expected longitude
     * @param expectedTime expected arrival time
     * @param checkpoint   checkpoint to verify
     * @param locationName location name for error messages
     */
    private void assertCheckpoint(double expectedLat, double expectedLng, final ZonedDateTime expectedTime,
                                  final Checkpoint checkpoint, final String locationName) {
        assertEquals(expectedLat, checkpoint.getLatitude(),
                String.format("La latitudine del checkpoint di %s non corrisponde", locationName));
        assertEquals(expectedLng, checkpoint.getLongitude(),
                String.format("La longitudine del checkpoint di %s non corrisponde", locationName));
        assertEquals(expectedTime, checkpoint.getArrivalDateTime(),
                String.format("L'orario di arrivo a %s non è corretto", locationName));
    }

    @Test
    void testGenerateCheckpointsWithSingleStep() {
        final LatLng start = new LatLng(41.9028, 12.4964);  // Roma
        final LatLng end = new LatLng(41.9109, 12.4818);    // Villa Borghese
        double duration = 600.0;  // 10 minuti
        double distance = 1500.0; // 1.5 km in metri

        final SimpleDirectionsStep step = new SimpleDirectionsStep(duration, end, start, distance);
        final List<SimpleDirectionsStep> steps = List.of(step);

        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertEquals(2, checkpoints.size(),
                "Il percorso con un singolo step dovrebbe generare 2 checkpoint (inizio e fine)");

        this.assertCheckpoint(start.getLat(), start.getLng(), departureTime,
                checkpoints.get(0), "punto di partenza");

        this.assertCheckpoint(end.getLat(), end.getLng(), departureTime.plusSeconds((long) duration),
                checkpoints.get(1), "punto di arrivo");
    }

    @Test
    void testGenerateCheckpointsWithMultipleSteps() {
        // Prima tappa: Roma -> Tivoli -> Frascati
        LatLng roma = new LatLng(41.9028, 12.4964);
        LatLng tivoli = new LatLng(41.9633, 12.7958);
        LatLng frascati = new LatLng(41.8089, 12.6799);

        SimpleDirectionsStep step1 = new SimpleDirectionsStep(1800.0, tivoli, roma, 25000.0);
        SimpleDirectionsStep step2 = new SimpleDirectionsStep(1200.0, frascati, tivoli, 15000.0);

        final List<SimpleDirectionsStep> steps = List.of(step1, step2);
        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertEquals(3, checkpoints.size(),
                "Il percorso con due step dovrebbe generare 3 checkpoint");

        this.assertCheckpoint(roma.getLat(), roma.getLng(), departureTime, checkpoints.get(0), "Roma");

        this.assertCheckpoint(tivoli.getLat(), tivoli.getLng(), departureTime.plusSeconds(1800),
                checkpoints.get(1), "Tivoli");

        this.assertCheckpoint(frascati.getLat(), frascati.getLng(), departureTime.plusSeconds(3000),
                checkpoints.get(2), "Frascati");

    }

    @Test
    void testGenerateCheckpointsReturnsImmutableList() {
        final LatLng start = new LatLng(41.9028, 12.4964);
        final LatLng end = new LatLng(41.9109, 12.4818);

        final SimpleDirectionsStep step = new SimpleDirectionsStep(600.0, end, start, 1500.0);

        final List<SimpleDirectionsStep> steps = List.of(step);
        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertThrows(UnsupportedOperationException.class,
                () -> checkpoints.add(new CheckpointImpl(0.0, 0.0, ZonedDateTime.now())),
                "La lista di checkpoint dovrebbe essere immutabile");
    }

    @Test
    void testGenerateCheckpointsWithZeroDuration() {
        final LatLng start = new LatLng(41.9028, 12.4964);
        final LatLng end = new LatLng(41.9109, 12.4818);

        final SimpleDirectionsStep step = new SimpleDirectionsStep(0.0, end, start, 100.0);

        final List<SimpleDirectionsStep> steps = List.of(step);
        final List<Checkpoint> checkpoints = generator.generateCheckpoints(steps, departureTime);

        assertEquals(checkpoints.get(0).getArrivalDateTime(), checkpoints.get(1).getArrivalDateTime(),
                "Per uno step con durata zero, i tempi di arrivo dovrebbero essere identici");
    }
}
