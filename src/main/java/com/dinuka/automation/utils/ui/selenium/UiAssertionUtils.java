package com.dinuka.automation.utils.ui.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class UiAssertionUtils {

    private UiAssertionUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Asserts that an element is displayed (visible in the DOM).
    public static void assertElementVisible(WebElement element) {
        Assert.assertTrue(element.isDisplayed(), "Element is not visible on the page.");
    }

    // Asserts that an element is enabled (interactable).
    public static void assertElementEnabled(WebElement element) {
        Assert.assertTrue(element.isEnabled(), "Element is not enabled.");
    }

    public static void assertElementDisabled(WebElement element) {
        Assert.assertFalse(element.isEnabled(), "Element is enabled, but it should be disabled.");
    }

    // Asserts that an element contains the expected text.
    public static void assertElementTextEquals(WebElement element, String expectedText) {
        String actualText = element.getText().trim();
        Assert.assertEquals(actualText, expectedText, "Element text does not match.");
    }

    // Asserts that an element contains a partial text.
    public static void assertElementContainsText(WebElement element, String partialText) {
        String actualText = element.getText();
        Assert.assertTrue(actualText.contains(partialText), "Element does not contain expected partial text.");
    }

    // Asserts that an element is not displayed (e.g., hidden or not present).
    public static void assertElementNotVisible(WebElement element) {
        Assert.assertFalse(element.isDisplayed(), "Element is visible, but expected to be hidden.");
    }

    // Asserts that an element is visible and contains text.
    public static void assertElementHasText(WebElement element) {
        String actualText = element.getText().trim();
        Assert.assertFalse(actualText.isEmpty(), "Element is visible but contains no text.");
    }

    // Asserts that the actual page title matches the expected title.
    public static void assertPageTitle(WebDriver driver, String expectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "Page title does not match.");
    }

    // Asserts that the actual page title does NOT match the given title.
    public static void assertPageTitleNot(WebDriver driver, String unexpectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertNotEquals(actualTitle, unexpectedTitle, "Page title unexpectedly matches.");
    }

    // Asserts that two strings (e.g., from fields) match exactly.
    public static void assertTextEquals(String actual, String expected) {
        Assert.assertEquals(actual, expected, "Text values do not match.");
    }

    // Asserts that the current URL contains the expected path or substring.
    public static void assertUrlContains(WebDriver driver, String expectedPartialUrl) {
        String actualUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                actualUrl.contains(expectedPartialUrl),
                "Expected URL to contain: " + expectedPartialUrl + " but was: " + actualUrl);
    }

    // Asserts that the current URL exactly matches the expected URL.
    public static void assertUrlEquals(WebDriver driver, String expectedUrl) {
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals(
                actualUrl,
                expectedUrl,
                "Expected URL to be exactly: " + expectedUrl + " but was: " + actualUrl);
    }

}
