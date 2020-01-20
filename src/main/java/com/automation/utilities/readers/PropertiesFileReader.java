package com.automation.utilities.readers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class PropertiesFileReader {
    private String propertyFilePath;

    public PropertiesFileReader(String propertyFilePath){
        this.propertyFilePath = propertyFilePath;
    }

    public String getProperty(String propertyKey){
        String property = null;
        try {
            property = readPropertiesFile().getProperty(propertyKey);
        } catch (IOException e){
            e.printStackTrace();
        }
        return property;
    }

    private Properties readPropertiesFile() throws IOException {
        FileInputStream propertiesFileInputStream = null;
        Properties properties = null;
        try {
            propertiesFileInputStream = new FileInputStream(propertyFilePath);
            properties = new Properties();
            properties.load(propertiesFileInputStream);
        } catch(IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            Objects.requireNonNull(propertiesFileInputStream).close();
        }
        return properties;
    }
}
