package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;

public class ConditionUtils {

    private ConditionUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Check if element is visible on screen
    public static boolean isElementDisplayed(Locator locator) {
        try {
            return locator.isVisible();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Overloaded method to check visibility using string selector
    public static boolean isElementVisible(Page page, String selector) {
        try {
            Locator locator = page.locator(selector);
            return locator.isVisible();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Check if element is enabled (interactable)
    public static boolean isElementEnabled(Locator locator) {
        try {
            return locator.isEnabled();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Check if element is selected (for checkboxes, radios, etc.)
    public static boolean isElementSelected(Locator locator) {
        try {
            return locator.isChecked();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Check if an element exists in the DOM
    // count() returns 0 if element doesn't exist, >0 if it does
    public static boolean doesElementExist(Page page, String selector) {
        try {
            return page.locator(selector).count() > 0;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Check if specific text is present in an element
    public static boolean isTextPresentInElement(Locator locator, String expectedText) {
        try {
            String elementText = locator.textContent();
            return elementText != null && elementText.contains(expectedText);
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Check if specific text is present in the entire page source
    public static boolean isTextPresentInPage(Page page, String text) {
        try {
            String pageSource = page.content();
            return pageSource.contains(text);
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Wait for element to be visible (explicit wait built-in)
    public static void waitForElementVisible(Locator locator, int timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
    }

    // Wait for element to be hidden
    public static void waitForElementHidden(Locator locator, int timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs)
                .setState(WaitForSelectorState.HIDDEN));
    }

    // Check if element is hidden (opposite of visible)
    public static boolean isElementHidden(Locator locator) {
        try {
            return locator.isHidden();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Get exact text content of element (trimmed)
    public static String getElementText(Locator locator) {
        try {
            String text = locator.textContent();
            return text != null ? text.trim() : "";
        } catch (PlaywrightException e) {
            return "";
        }
    }

    // Check if element has specific attribute value
    public static boolean hasAttributeValue(Locator locator, String attributeName, String expectedValue) {
        try {
            String actualValue = locator.getAttribute(attributeName);
            return actualValue != null && actualValue.equals(expectedValue);
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Count matching elements
    public static int countElements(Page page, String selector) {
        try {
            return page.locator(selector).count();
        } catch (PlaywrightException e) {
            return 0;
        }
    }

}
