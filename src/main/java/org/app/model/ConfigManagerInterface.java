package org.app.model;

public interface ConfigManagerInterface {

    void loadConfig(String filePath) throws Exception;

    AppConfig getConfig() throws Exception;

    void saveConfig(String filePath) throws Exception;

}