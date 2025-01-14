package org.app.model;

import java.io.IOException;

public interface ConfigManagerInterface {

    void loadConfig(String filePath) throws IOException;

    AppConfig getConfig();

    void saveConfig(String filePath) throws IOException;

}