package com.dinuka.automation.utils.ui.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabActions {
    private static final Logger logger = LoggerFactory.getLogger(TabActions.class);

    private WebDriver driver;

    private TabActions() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Constructor to initialize the WebDriver
    public TabActions(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Opens a new tab in the browser.
     * 
     * Steps:
     * 1. Switch to a new tab by opening a new window.
     * 2. Switch to the newly opened tab.
     */
    public void openNewTab() {
        driver.switchTo().newWindow(WindowType.TAB);
    }

    /**
     * Switches the driver to a specific tab by index.
     * 
     * tabIndex - The index of the tab to switch to (starting from 0).
     */
    public void switchToTab() {
        String currentWindowHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    /**
     * Closes the current tab that the driver is focused on.
     * 
     * Note: The driver will automatically switch to another open tab after closing
     * the current one.
     */
    public void closeCurrentTab() {
        driver.close();
    }

    /**
     * Switches back to the main window/tab.
     * 
     * This is useful after switching to a new tab and wanting to return to the
     * original one.
     */
    public void switchToMainTab() {
        String mainWindowHandle = driver.getWindowHandles().iterator().next();
        driver.switchTo().window(mainWindowHandle);
    }

    /*
     * Prints the title of the current tab to the console.
     */
    public void printCurrentTabTitle() {
        logger.info("Current tab title: " + driver.getTitle());
    }

    /**
     * Prints the URL of the current tab to the console.
     */
    public void printCurrentTabUrl() {
        logger.info("Current tab URL: " + driver.getCurrentUrl());
    }
}
