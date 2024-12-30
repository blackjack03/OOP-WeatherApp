package org.app.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

public class AdvancedJsonReader implements AdvancedJsonReaderImpl {
    private String json_raw_text;
    private JsonObject JSON_BODY;
    private boolean isSet = false;

    public AdvancedJsonReader() { /* empty body */ }

    public AdvancedJsonReader(final String jsonURL) throws Exception {
        requestJSON(jsonURL);
    }

    public AdvancedJsonReader(final JsonObject jsonObj) {
        this.json_raw_text = jsonObj.toString();
        parseAndSetJson();
    }

    @Override
    public void requestJSON(final String jsonURL) throws Exception {
        assertNotAlreadySet();
        final URL url = new URL(jsonURL);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        final StringBuilder jsonText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonText.append(line);
        }
        reader.close();
        connection.disconnect();

        this.json_raw_text = jsonText.toString();
        parseAndSetJson();
    }

    @Override
    public AdvancedJsonReader setJSON(final String jsonString) {
        assertNotAlreadySet();
        this.json_raw_text = jsonString;
        parseAndSetJson();
        return this;
    }

    @Override
    public void setJSON(final JsonObject jsonObj) {
        assertNotAlreadySet();
        this.json_raw_text = jsonObj.toString();
        parseAndSetJson();
    }

    @Override
    public String getRawJSON() {
        assertIsSet();
        return this.json_raw_text;
    }

    @Override
    public JsonObject walkthroughBody(final String path) throws Exception {
        this.assertIsSet();
        final String[] parts = path.split("\\.");
        if (parts.length == 0) {
            return null;
        }
        JsonObject OUTPUT = this.JSON_BODY.getAsJsonObject(parts[0]);
        if(OUTPUT == null) {
            throw new IllegalArgumentException("\"" + parts[0] + "\" not found!");
        }
        for (int i = 1; i < parts.length; i++) {
            OUTPUT = OUTPUT.getAsJsonObject(parts[i]);
            if (OUTPUT == null) {
                throw new IllegalArgumentException("In path: \"" + parts[i] + "\" no member with this name exists!");
            }
        }
        return OUTPUT;
    }

    @Override
    public JsonArray getJsonArray(final String path) throws Exception {
        assertIsSet();
        final String[] parts = path.split("\\.");

        final String newPath = getLevelUpPath(parts);

        JsonArray jsonArray;
        if(parts.length > 1) {
            jsonArray = this.walkthroughBody(newPath).get(parts[parts.length-1]).getAsJsonArray();
        } else {
            jsonArray = this.JSON_BODY.get(parts[0]).getAsJsonArray();
        }

        return jsonArray;
    }

    @Override
    public <T> T getFromJson(final String path, final Class<T> type) throws Exception {
        assertIsSet();
        T outElem = null;
        final String[] parts = path.split("\\.");
        if (parts.length == 0 || path.trim().isEmpty()) {
            return null;
        }
        final String ELEM_TO_GET = parts[parts.length - 1];

        final String newPath = getLevelUpPath(parts);

        JsonElement ELEMENT;
        if(parts.length > 1) {
            final JsonObject finalLevel = this.walkthroughBody(newPath);
            ELEMENT = (JsonElement)finalLevel.get(ELEM_TO_GET);
        } else {
            ELEMENT = this.JSON_BODY.get(ELEM_TO_GET);
        }

        if(ELEMENT == null) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists!");
        }

        if (type.equals(String.class)) {
            outElem = type.cast(ELEMENT.getAsString());
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            outElem = type.cast(ELEMENT.getAsBoolean());
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            outElem = type.cast(ELEMENT.getAsDouble());
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            outElem = type.cast(ELEMENT.getAsFloat());
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            outElem = type.cast(ELEMENT.getAsInt());
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            outElem = type.cast(ELEMENT.getAsLong());
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            outElem = type.cast(ELEMENT.getAsShort());
        } else if (type.equals(JsonArray.class) ||
                    type.equals(JsonObject.class) ||
                    type.equals(JsonPrimitive.class)) {
            outElem = type.cast(ELEMENT);
        } else if (type.equals(Number.class)) {
            outElem = type.cast(ELEMENT.getAsNumber());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        return outElem;
    }

    @Override
    public JsonElement getFromJson(final String path) throws Exception {
        assertIsSet();
        final String[] parts = path.split("\\.");
        final String ELEM_TO_GET = parts[parts.length - 1];

        final String newPath = getLevelUpPath(parts);

        JsonElement ELEMENT;
        if(parts.length > 1) {
            final JsonObject finalLevel = this.walkthroughBody(newPath);
            ELEMENT = (JsonElement)finalLevel.get(ELEM_TO_GET);
        } else {
            ELEMENT = this.JSON_BODY.get(ELEM_TO_GET);
        }

        if(ELEMENT == null) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists!");
        }

        return ELEMENT;
    }

    @Override
    public boolean elementExists(final String path) {
        try {
            final var elem = this.getFromJson(path);
            return true;
        } catch(final Exception e) {
            return false;
        }
    }

    @Override
    public String getString(final String path) {
        try {
            return this.getFromJson(path, String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists or is not a String!");
        }
    }

    @Override
    public Integer getInt(final String path) {
        try {
            return this.getFromJson(path, Integer.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists or is not a Integer!");
        }
    }

    @Override
    public Long getLong(final String path) {
        try {
            return this.getFromJson(path, Long.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists or is not a Long!");
        }
    }

    @Override
    public Double getDouble(final String path) {
        try {
            return this.getFromJson(path, Double.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists or is not a Double!");
        }
    }

    @Override
    public Float getFloat(final String path) {
        try {
            return this.getFromJson(path, Float.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists or is not a Float!");
        }
    }

    @Override
    public boolean getBool(final String path) {
        try {
            return this.getFromJson(path, Boolean.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("\"" + path + "\": no such element with this name exists or is not a boolean!");
        }
    }

    /* Private Methods */

    private void assertNotAlreadySet() throws IllegalStateException {
        if (this.isSet) {
            throw new IllegalStateException("This AdvancedJsonReader Object was alreday set!");
        }
    }

    private void assertIsSet() throws UnsupportedOperationException {
        if (!this.isSet) {
            throw new UnsupportedOperationException("No JSON was set!");
        }
    }

    private void parseAndSetJson() {
        this.JSON_BODY = JsonParser.parseString(this.json_raw_text).getAsJsonObject();
        this.isSet = true;
    }

    private static String getLevelUpPath(final String[] parts) {
        final StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            newPath.append(parts[i]);
            if (i < parts.length - 2) {
                newPath.append(".");
            }
        }
        return newPath.toString();
    }

}
