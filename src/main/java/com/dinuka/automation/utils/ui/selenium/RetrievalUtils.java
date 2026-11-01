package com.dinuka.automation.utils.ui.selenium;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.Point;
import  org.openqa.selenium.Dimension;

public class RetrievalUtils {

    private RetrievalUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Get the visible text from an element
    public static String getElementText(WebElement element) {
        return element.getText();
    }

    // Get the visible text from an element and trim whitespace
    public static String getTrimmedText(WebElement element) {
        return element.getText().trim();
    }    

    // Get the value of a specific attribute of an element
    public static String getAttribute(WebElement element, String attributeName) {
        return element.getDomProperty(attributeName);
    }

    // get the typed value in a text box
    public static String getInputValue(WebElement element) {
        return element.getDomProperty("value");
    }

    // Get the value of a specific CSS property
    public static String getCssValue(WebElement element, String cssProperty) {
        return element.getCssValue(cssProperty);
    }

    // Get the tag name of the element (e.g., "input", "div")
    public static String getTagName(WebElement element) {
        return element.getTagName();
    }

    // Get the location (x, y coordinates) of the element on the page
    public static String getLocationString(WebElement element) {
        return element.getLocation().toString();
    }

    // Get the location (x, y coordinates) of the element on the page as Point
    public static Point getLocation(WebElement element) {
        return element.getLocation();
    }

    // Get the dimensions (height and width) of the element
    public static String getSizeString(WebElement element) {
        return element.getSize().toString();
    }

    // Get the dimensions (height and width) of the element as Dimension
    public static Dimension getSize(WebElement element) {
        return element.getSize();
    }

    // Get the value of a link text from an element
    public static String getLinkText(WebElement element) {
        return element.getText();
    }

    // Get the value of a link href attribute from an element
    public static String getLinkHref(WebElement element) {
        return element.getDomAttribute("href");
    }
    
    
}
