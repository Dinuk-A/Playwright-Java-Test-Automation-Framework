package com.dinuka.automation.utils.ui.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class WaitUtils {

    // to prevent instantiation
    private WaitUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // FOR VISIBILITY
    // Custom timeout version
    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Default timeout version (uses 10 seconds)
    public static WebElement waitForElementVisible(WebDriver driver, By locator) {
        return waitForElementVisible(driver, locator, 10);
    }

    // FOR CLICKABLE
    // Custom timeout version
    public static WebElement waitForElementClickable(WebDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // Default timeout version (uses 10 seconds)
    public static WebElement waitForElementClickable(WebDriver driver, By locator) {
        return waitForElementClickable(driver, locator, 10);
    }

    // Presence in DOM(not necessarily visible)
    // Custom timeout version
    public static WebElement waitForElementPresence(WebDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // Default timeout version (uses 10 seconds)
    public static WebElement waitForElementPresence(WebDriver driver, By locator) {
        return waitForElementPresence(driver, locator, 10);
    }

    // Text to be present in element
    // Custom timeout version
    public static boolean waitForTextInElement(WebDriver driver, By locator, String text, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // Default timeout version (uses 10 seconds)
    public static boolean waitForTextInElement(WebDriver driver, By locator, String text) {
        return waitForTextInElement(driver, locator, text, 10);
    }

    // Alert is present
    // Custom timeout version
    public static Alert waitForBrowserPopup(WebDriver driver, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    // Default timeout version (uses 10 seconds)
    public static Alert waitForBrowserPopup(WebDriver driver) {
        return waitForBrowserPopup(driver, 10);
    }

    // Wait until the URL contains a specific string (custom timeout)
    public static void waitForUrlContains(WebDriver driver, String partialUrl, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.urlContains(partialUrl));
    }

    // Overloaded version with default timeout (10 seconds)
    public static void waitForUrlContains(WebDriver driver, String partialUrl) {
        waitForUrlContains(driver, partialUrl, 10);
    }

}
