package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.testng.Assert;

public class UiAssertionUtils {

    /*
     * Asserts that an element is visible on the page.
     */

    public static final String ELEMENTTEXTNULL = "Element text is null.";
    public static final String ATTRIBUTE ="Attribute '";

    private UiAssertionUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void assertElementVisible(Locator locator) {
        try {
            Assert.assertTrue(locator.isVisible(), "Element is not visible on the page.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element visibility: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element is enabled (interactable).
     */
    public static void assertElementEnabled(Locator locator) {
        try {
            Assert.assertTrue(locator.isEnabled(), "Element is not enabled.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element enabled state: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element is disabled (not interactable).
     */
    public static void assertElementDisabled(Locator locator) {
        try {
            Assert.assertFalse(locator.isEnabled(), "Element is enabled, but it should be disabled.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element disabled state: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element contains the expected exact text.
     */
    public static void assertElementTextEquals(Locator locator, String expectedText) {
        try {
            String actualText = locator.textContent();
            Assert.assertNotNull(actualText, ELEMENTTEXTNULL);
            Assert.assertEquals(actualText.trim(), expectedText.trim(), "Element text does not match.");
        } catch (PlaywrightException e) {
            Assert.fail("Error getting element text: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element contains partial text (substring match).
     */
    public static void assertElementContainsText(Locator locator, String partialText) {
        try {
            String actualText = locator.textContent();
            Assert.assertNotNull(actualText, ELEMENTTEXTNULL);
            Assert.assertTrue(actualText.contains(partialText),
                    "Element does not contain expected partial text: '" + partialText + "'. Actual: '" + actualText
                            + "'");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element text content: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element is not visible (hidden or not present).
     */
    public static void assertElementNotVisible(Locator locator) {
        try {
            Assert.assertTrue(locator.isHidden(), "Element is visible, but expected to be hidden.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element visibility: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element is visible and contains non-empty text.
     */
    public static void assertElementHasText(Locator locator) {
        try {
            Assert.assertTrue(locator.isVisible(), "Element is not visible.");
            String actualText = locator.textContent();
            Assert.assertNotNull(actualText, ELEMENTTEXTNULL);
            Assert.assertFalse(actualText.trim().isEmpty(), "Element is visible but contains no text.");
        } catch (PlaywrightException e) {
            Assert.fail("Error verifying element has text: " + e.getMessage());
        }
    }

    /*
     * Asserts that the actual page title matches the expected title.
     */
    public static void assertPageTitle(Page page, String expectedTitle) {
        try {
            String actualTitle = page.title();
            Assert.assertEquals(actualTitle, expectedTitle, "Page title does not match.");
        } catch (PlaywrightException e) {
            Assert.fail("Error getting page title: " + e.getMessage());
        }
    }

    /*
     * Asserts that the actual page title does NOT match the given title.
     */
    public static void assertPageTitleNot(Page page, String unexpectedTitle) {
        try {
            String actualTitle = page.title();
            Assert.assertNotEquals(actualTitle, unexpectedTitle, "Page title unexpectedly matches.");
        } catch (PlaywrightException e) {
            Assert.fail("Error getting page title: " + e.getMessage());
        }
    }

    /*
     * Asserts that two strings (e.g., from fields) match exactly.
     */
    public static void assertTextEquals(String actual, String expected) {
        Assert.assertEquals(actual, expected, "Text values do not match.");
    }

    /*
     * Asserts that the current URL contains the expected path or substring.
     */
    public static void assertUrlContains(Page page, String expectedPartialUrl) {
        try {
            String actualUrl = page.url();
            Assert.assertTrue(
                    actualUrl.contains(expectedPartialUrl),
                    "Expected URL to contain: " + expectedPartialUrl + " but was: " + actualUrl);
        } catch (PlaywrightException e) {
            Assert.fail("Error getting page URL: " + e.getMessage());
        }
    }

    /*
     * Asserts that the current URL exactly matches the expected URL.
     */
    public static void assertUrlEquals(Page page, String expectedUrl) {
        try {
            String actualUrl = page.url();
            Assert.assertEquals(
                    actualUrl,
                    expectedUrl,
                    "Expected URL to be exactly: " + expectedUrl + " but was: " + actualUrl);
        } catch (PlaywrightException e) {
            Assert.fail("Error getting page URL: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element exists in the DOM (present but may not be visible).
     */
    public static void assertElementExists(Locator locator) {
        try {
            int count = locator.count();
            Assert.assertTrue(count > 0, "Element does not exist in the DOM.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element existence: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element does NOT exist in the DOM.
     */
    public static void assertElementNotExists(Locator locator) {
        try {
            int count = locator.count();
            Assert.assertEquals(count, 0, "Element exists in the DOM, but should not.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element non-existence: " + e.getMessage());
        }
    }

    /*
     * Asserts that a checkbox or radio button is checked.
     */
    public static void assertElementChecked(Locator locator) {
        try {
            Assert.assertTrue(locator.isChecked(), "Element is not checked.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element checked state: " + e.getMessage());
        }
    }

    /*
     * Asserts that a checkbox or radio button is NOT checked.
     */
    public static void assertElementNotChecked(Locator locator) {
        try {
            Assert.assertFalse(locator.isChecked(), "Element is checked, but should be unchecked.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking element unchecked state: " + e.getMessage());
        }
    }

    /*
     * Asserts that an attribute has a specific value.
     */
    public static void assertAttributeEquals(Locator locator, String attributeName, String expectedValue) {
        try {
            String actualValue = locator.getAttribute(attributeName);
            Assert.assertEquals(actualValue, expectedValue,
                    ATTRIBUTE + attributeName + "' does not match. Expected: '" + expectedValue + "', Actual: '"
                            + actualValue + "'");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking attribute value: " + e.getMessage());
        }
    }

    /*
     * Asserts that an attribute contains a partial value (substring match).
     */
    public static void assertAttributeContains(Locator locator, String attributeName, String partialValue) {
        try {
            String actualValue = locator.getAttribute(attributeName);
            Assert.assertNotNull(actualValue, ATTRIBUTE + attributeName + "' is null.");
            Assert.assertTrue(actualValue.contains(partialValue),
                    ATTRIBUTE + attributeName + "' does not contain '" + partialValue + "'. Actual: '" + actualValue
                            + "'");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking attribute contains: " + e.getMessage());
        }
    }

    /*
     * Asserts that a CSS class is present on an element.
     */
    public static void assertHasClass(Locator locator, String className) {
        try {
            String classes = locator.getAttribute("class");
            Assert.assertNotNull(classes, "Element has no class attribute.");
            Assert.assertTrue(classes.contains(className),
                    "Element does not have class '" + className + "'. Actual classes: '" + classes + "'");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking class presence: " + e.getMessage());
        }
    }

    /*
     * Asserts that a CSS class is NOT present on an element.
     */
    public static void assertNotHasClass(Locator locator, String className) {
        try {
            String classes = locator.getAttribute("class");
            Assert.assertFalse(classes != null && classes.contains(className),
                    "Element has class '" + className + "', but should not.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking class absence: " + e.getMessage());
        }
    }

    /*
     * Asserts that an element has a specific placeholder text (for inputs).
     */
    public static void assertPlaceholder(Locator locator, String expectedPlaceholder) {
        try {
            String actualPlaceholder = locator.getAttribute("placeholder");
            Assert.assertEquals(actualPlaceholder, expectedPlaceholder,
                    "Placeholder text does not match.");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking placeholder: " + e.getMessage());
        }
    }

    /*
     * Asserts that an input field has a specific value.
     */
    public static void assertInputValue(Locator locator, String expectedValue) {
        try {
            String actualValue = locator.inputValue();
            Assert.assertEquals(actualValue, expectedValue,
                    "Input value does not match. Expected: '" + expectedValue + "', Actual: '" + actualValue + "'");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking input value: " + e.getMessage());
        }
    }

    /*
     * Asserts that a specific number of elements match a locator.
     */
    public static void assertElementCount(Locator locator, int expectedCount) {
        try {
            int actualCount = locator.count();
            Assert.assertEquals(actualCount, expectedCount,
                    "Element count does not match. Expected: " + expectedCount + ", Actual: " + actualCount);
        } catch (PlaywrightException e) {
            Assert.fail("Error counting elements: " + e.getMessage());
        }
    }

    /*
     * Asserts that the page contains specific text somewhere (in the entire DOM).
     */
    public static void assertPageContainsText(Page page, String expectedText) {
        try {
            String pageContent = page.content();
            Assert.assertTrue(pageContent.contains(expectedText),
                    "Page does not contain expected text: '" + expectedText + "'");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking page content: " + e.getMessage());
        }
    }

    /*
     * Asserts that the page does NOT contain specific text.
     */
    public static void assertPageNotContainsText(Page page, String unexpectedText) {
        try {
            String pageContent = page.content();
            Assert.assertFalse(pageContent.contains(unexpectedText),
                    "Page contains unexpected text: '" + unexpectedText + "'");
        } catch (PlaywrightException e) {
            Assert.fail("Error checking page content: " + e.getMessage());
        }
    }
}