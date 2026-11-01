package com.dinuka.automation.utils.ui.selenium;

import java.util.Set;

import org.openqa.selenium.WebDriver;

public class BrowserUtils {

    private BrowserUtils() {
         throw new UnsupportedOperationException("Utility class");
    }

    // Opens a URL in the current browser window.
    public static void openUrl(WebDriver driver, String url) {
        driver.get(url);
    }

    // Navigates back in the browser history.
    public static void goBack(WebDriver driver) {
        driver.navigate().back();
    }

    // Navigates forward in the browser history.
    public static void goForward(WebDriver driver) {
        driver.navigate().forward();
    }

    // Refreshes the current page.
    public static void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
    }

    // Retrieves the current URL of the browser.
    public static String getCurrentUrl(WebDriver driver) {
        return driver.getCurrentUrl();
    }

    // Retrieves the title of the current page.
    public static String getPageTitle(WebDriver driver) {
        return driver.getTitle();
    }

    // Maximizes the browser window.
    public static void maximizeWindow(WebDriver driver) {
        driver.manage().window().maximize();
    }

    // Sets the browser window to fullscreen mode.
    public static void fullscreenWindow(WebDriver driver) {
        driver.manage().window().fullscreen();
    }

    // Switches to a different browser window or tab by its handle.
    public static void switchToWindow(WebDriver driver, String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    // Retrieves the handle of the current browser window.
    public static String getCurrentWindowHandle(WebDriver driver) {
        return driver.getWindowHandle();
    }

    // Retrieves all open browser window handles.
    public static Set<String> getAllWindowHandles(WebDriver driver) {
        return driver.getWindowHandles();
    }

}
