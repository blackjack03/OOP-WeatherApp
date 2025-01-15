package org.app.travelmode.model;

import javafx.scene.image.Image;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.List;

public class MapImageGeneratorImpl implements MapImageGenerator {

    private final String googleApiKey;

    public MapImageGeneratorImpl(final String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    @Override
    public Image generateMapImage(final List<CheckpointWithMeteo> checkpoints, final String polyline) {
        try {
            final StringBuilder urlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=650x370&scale=2&path=enc:");
            urlBuilder.append(polyline).append("&key=").append(googleApiKey);

            for (final CheckpointWithMeteo checkpoint : checkpoints) {
                int weatherScore = checkpoint.getWeatherScore();

                switch (WeatherScoreCategory.fromScore(weatherScore)) {
                    case GOOD:
                        addMarker(urlBuilder, checkpoint, "yellow");
                        break;
                    case BAD:
                        addMarker(urlBuilder, checkpoint, "orange");
                        break;
                    case TERRIBLE:
                        addMarker(urlBuilder, checkpoint, "red");
                        break;
                    default:
                        break;
                }
            }

            final String url = urlBuilder.toString();

            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Errore durante la richiesta della mappa: " + connection.getResponseMessage());
            }

            final InputStream inputStream = connection.getInputStream();
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addMarker(final StringBuilder urlBuilder, final CheckpointWithMeteo checkpoint, final String color) {
        urlBuilder.append("&markers=color:").append(color + "%7C")
                //.append("%7Clabel:%7C")
                .append(checkpoint.getLatitude()).append(",")
                .append(checkpoint.getLongitude());
    }
}

