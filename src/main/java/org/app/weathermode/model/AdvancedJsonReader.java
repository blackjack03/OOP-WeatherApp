package org.app.weathermode.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public interface AdvancedJsonReader {

    void requestJSON(String jsonURL) throws IOException;

    AdvancedJsonReaderImpl setJSON(String jsonString);

    void setJSON(JsonObject jsonObj);

    String getRawJSON();

    JsonObject walkthroughBody(String path) throws Exception;

    JsonArray getJsonArray(String path) throws Exception;

    <T> T getFromJson(String path, Class<T> type) throws Exception;

    JsonElement getFromJson(String path) throws Exception;

    boolean elementExists(String path);

    String getString(String path);

    Integer getInt(String path);

    Long getLong(String path);

    Double getDouble(String path);

    Float getFloat(String path);

    boolean getBool(String path);

}
