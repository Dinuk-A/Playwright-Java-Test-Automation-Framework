package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Locator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetrievalUtils {

    private static final Logger logger = LoggerFactory.getLogger(RetrievalUtils.class);

    private RetrievalUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Get the visible text from an element
    public static String getElementText(Locator locator) {
        try {
            String text = locator.textContent();
            return text != null ? text : "";
        } catch (PlaywrightException e) {
            logger.info("Error getting element text: " + e.getMessage());
            return null;
        }
    }

    // Get the visible text from an element and trim whitespace
    public static String getTrimmedText(Locator locator) {
        try {
            String text = locator.textContent();
            return text != null ? text.trim() : "";
        } catch (PlaywrightException e) {
            logger.info("Error getting trimmed text: " + e.getMessage());
            return null;
        }
    }

    // Get the value of a specific attribute of an element
    public static String getAttribute(Locator locator, String attributeName) {
        try {
            String value = locator.getAttribute(attributeName);
            return value != null ? value : "";
        } catch (PlaywrightException e) {
            logger.info("Error getting attribute '" + attributeName + "': " + e.getMessage());
            return null;
        }
    }

    // Get the typed value in a text box (value attribute)
    public static String getInputValue(Locator locator) {
        try {
            String value = locator.inputValue();
            return value != null ? value : "";
        } catch (PlaywrightException e) {
            logger.info("Error getting input value: " + e.getMessage());
            return null;
        }
    }

    // Get the value of a specific CSS property
    public static String getCssValue(Locator locator, String cssProperty) {
        try {
            String value = locator
                    .evaluate("(element, prop) => window.getComputedStyle(element).getPropertyValue(prop)",
                            new Object[] { cssProperty })
                    .toString();
            return value != null ? value : "";
        } catch (PlaywrightException e) {
            logger.info("Error getting CSS value for '" + cssProperty + "': " + e.getMessage());
            return null;
        }
    }

    // Get the tag name of the element (e.g., "input", "div")
    public static String getTagName(Locator locator) {
        try {
            return locator.evaluate("element => element.tagName.toLowerCase()").toString();
        } catch (PlaywrightException e) {
            logger.info("Error getting tag name: " + e.getMessage());
            return null;
        }
    }

    // Get the location (x, y coordinates) of the element on the page as string
    public static String getLocationString(Locator locator) {
        try {
            Object boundingBox = locator.boundingBox();
            if (boundingBox != null) {
                return boundingBox.toString();
            }
            return null;
        } catch (PlaywrightException e) {
            logger.info("Error getting location: " + e.getMessage());
            return null;
        }
    }

    // Get the location (x, y coordinates) of the element on the page
    public static Object getLocation(Locator locator) {
        try {
            return locator.boundingBox();
        } catch (PlaywrightException e) {
            logger.info("Error getting location: " + e.getMessage());
            return null;
        }
    }

    // Get the visible text from a link element
    public static String getLinkText(Locator locator) {
        try {
            String text = locator.textContent();
            return text != null ? text.trim() : "";
        } catch (PlaywrightException e) {
            logger.info("Error getting link text: " + e.getMessage());
            return null;
        }
    }

    // Get the href attribute value from a link element
    public static String getLinkHref(Locator locator) {
        try {
            String href = locator.getAttribute("href");
            return href != null ? href : "";
        } catch (PlaywrightException e) {
            logger.info("Error getting link href: " + e.getMessage());
            return null;
        }
    }

}
