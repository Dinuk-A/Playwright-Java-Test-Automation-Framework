package com.dinuka.automation.utils.ui.core;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class PlaywrightConfig {

    private static final Properties properties;

    private PlaywrightConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    static {
        try {
            Properties props = new Properties();
            InputStream input = PlaywrightConfig.class.getClassLoader()
                    .getResourceAsStream("playwright-config.properties");
            if (input != null) {
                props.load(input);
            } else {
                throw new IllegalStateException("playwright-config.properties not found in classpath");
            }
            properties = props;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load playwright config file", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getBrowser() {
        return properties.getProperty("playwright.browser", "chromium");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("playwright.headless", "true"));
    }

    public static int getDefaultTimeout() {
        return Integer.parseInt(properties.getProperty("playwright.defaultTimeout", "30000"));
    }
}