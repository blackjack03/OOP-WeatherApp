package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;

import java.io.FileReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TravelModeResultImpl implements TravelModeResult{

    private final List<CheckpointWithMeteo> checkpoints;
    private final String summary;
    private final String polyline;
    private Image mapImage;
    private String googleApiKey;

    public TravelModeResultImpl(final List<CheckpointWithMeteo> checkpoints, final String summary, final String polyline) {
        this.checkpoints = checkpoints;
        this.summary = summary;
        this.polyline = polyline;
        //TODO: delegare
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CheckpointWithMeteo> getCheckpoints() {
        return List.copyOf(checkpoints);
    }

    @Override
    public Image getMapImage() {
        //TODO: delegare
        double startLatitude = this.checkpoints.get(0).getLatitude();
        double startLongitude = this.checkpoints.get(0).getLongitude();
        String url = "https://maps.googleapis.com/maps/api/staticmap?size=400x400&scale=2&" +
                "markers=color:blue%7Clabel:P%7C" + startLatitude + "," + startLongitude +
                "&path=enc:" + this.polyline +
                "&key=" + googleApiKey;
        try {
            // Connessione HTTP per ottenere l'immagine
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Controlla se la richiesta è stata eseguita con successo
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Errore durante la richiesta della mappa: " + connection.getResponseMessage());
            }

            // Ottieni l'immagine come InputStream
            InputStream inputStream = connection.getInputStream();

            // Crea un oggetto Image di JavaFX dall'InputStream
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getMeteoScore() {
        return 0;
    }

    @Override
    public String getSummary() {
        return this.summary;
    }
}
