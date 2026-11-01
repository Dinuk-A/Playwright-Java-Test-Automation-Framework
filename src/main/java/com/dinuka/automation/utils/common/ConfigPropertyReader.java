package com.dinuka.automation.utils.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//to read the config.properties file and extract the values and urls  ✅
public class ConfigPropertyReader {

    private static Map<String, Properties> propertiesCache = new HashMap<>();

    private ConfigPropertyReader() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Load default config.properties
    static {
        loadProperties("config.properties");
    }

    // Load properties from a specific file
    private static void loadProperties(String fileName) {
        try {
            InputStream input = ConfigPropertyReader.class.getClassLoader()
                    .getResourceAsStream(fileName);

            if (input == null) {
                throw new IllegalStateException(fileName + " file not found in classpath!");
            }

            Properties props = new Properties();
            props.load(input);
            propertiesCache.put(fileName, props);
            input.close();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config file: " + fileName, e);
        }
    }

    // for default config.properties
    public static String getProperty(String key) {
        return propertiesCache.get("config.properties").getProperty(key);
    }

    // for specific files
    public static String getProperty(String fileName, String key) {
        if (!propertiesCache.containsKey(fileName)) {
            loadProperties(fileName);
        }
        return propertiesCache.get(fileName).getProperty(key);
    }

}
