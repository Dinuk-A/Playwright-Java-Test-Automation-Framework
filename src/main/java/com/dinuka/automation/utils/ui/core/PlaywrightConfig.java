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

            try (InputStream input = PlaywrightConfig.class
                    .getClassLoader()
                    .getResourceAsStream("playwright-config.properties")) {

                if (input == null) {
                    throw new IllegalStateException(
                            "playwright-config.properties not found in classpath");
                }

                props.load(input);
            }

            properties = props;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load Playwright configuration", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Browser configuration
    public static String getBrowser() {
        return properties.getProperty("playwright.browser", "chromium");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(
                properties.getProperty("playwright.headless", "true"));
    }

    public static int getSlowMo() {
        return Integer.parseInt(
                properties.getProperty("playwright.slowmo", "0"));
    }

    // Viewport configuration
    public static int getViewportWidth() {
        return Integer.parseInt(
                properties.getProperty("playwright.viewport.width", "1280"));
    }

    public static int getViewportHeight() {
        return Integer.parseInt(
                properties.getProperty("playwright.viewport.height", "720"));
    }

    // Timeout configuration
    public static int getDefaultTimeout() {
        return Integer.parseInt(
                properties.getProperty("playwright.timeout.default", "30000"));
    }

    public static int getNavigationTimeout() {
        return Integer.parseInt(
                properties.getProperty("playwright.timeout.navigation", "30000"));
    }
}