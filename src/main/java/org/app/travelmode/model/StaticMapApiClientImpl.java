package org.app.travelmode.model;

import javafx.scene.image.Image;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.List;

public class StaticMapApiClientImpl extends AbstractGoogleApiClient implements StaticMapApiClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    private static final String DEFAULT_SIZE = "650x488";
    private static final String DEFAULT_SCALE = "2";
    private static final String DEFAULT_LANGUAGE = "it";

    private final GoogleApiRequestBuilder requestBuilder;

    public StaticMapApiClientImpl(final String apiKey) {
        super(BASE_URL, apiKey);
        this.requestBuilder = new GoogleApiRequestBuilderImpl(BASE_URL, this.getApiKey());
    }

    @Override
    public Image generateMapImage(final List<CheckpointWithMeteo> checkpoints, final String polyline) {
        try {
            final String url = buildMapUrl(checkpoints, polyline);

            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Errore durante la richiesta della mappa: " + connection.getResponseMessage());
            }

            final InputStream inputStream = connection.getInputStream();
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildMapUrl(final List<CheckpointWithMeteo> checkpoints, final String polyline) {
        this.requestBuilder.addParameter("size", DEFAULT_SIZE)
                .addParameter("scale", DEFAULT_SCALE)
                .addParameter("language", DEFAULT_LANGUAGE)
                .addParameter("path", "enc:" + polyline);

        for (final CheckpointWithMeteo checkpoint : checkpoints) {
            addCheckpointMarker(checkpoint);
        }
        return this.requestBuilder.build();
    }

    private void addCheckpointMarker(final CheckpointWithMeteo checkpoint) {
        final String color = selectMarkerColor(checkpoint.getWeatherScore());
        if (color != null) {
            this.requestBuilder.addParameter("markers", String.format("color:%s|%f,%f",
                    color,
                    checkpoint.getLatitude(),
                    checkpoint.getLongitude())
            );
        }
    }

    private String selectMarkerColor(int weatherScore) {
        return switch (WeatherScoreCategory.fromScore(weatherScore)) {
            case GOOD -> "yellow";
            case BAD -> "orange";
            case TERRIBLE -> "red";
            default -> null;
        };
    }

}

