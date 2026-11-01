package com.dinuka.automation.utils.ui.selenium;

import java.util.NoSuchElementException;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

// This class is a placeholder for element actions.
public class ElementActions {

    private ElementActions() {
        throw new UnsupportedOperationException("Utility class");
    }

    private WebDriver driver;

    // Constructor to initialize the WebDriver ✅
    public ElementActions(WebDriver driver) {
        this.driver = driver;
    }

    // Click on an element, using Actions as fallback if normal click fails ✅
    public void click(WebElement element) {
        try {
            element.click();
        } catch (ElementNotInteractableException e) {
            Actions actions = new Actions(driver);
            actions.moveToElement(element)
                    .click()
                    .perform();
        }
    }

    // Javascript click on an element ✅
    // Sometimes elements overlap (e.g., modals or pop-ups), preventing normal
    // clicks. JavaScript can help bypass this.
    public void jsClick(WebElement element) {
        String script = "arguments[0].click();";
        ((JavascriptExecutor) driver).executeScript(script, element);
    }

    // Double click on an element ✅
    public void doubleClick(WebElement element) {
        Actions actions = new Actions(driver);
        actions.doubleClick(element).perform();
    }

    // Right click on an element ✅
    public void rightClick(WebElement element) {
        Actions actions = new Actions(driver);
        actions.contextClick(element).perform();
    }

    // Click on an element by its text - Updated to be consistent ✅
    public void clickLink(WebElement linkElement) {
        if (linkElement == null) {
            throw new NoSuchElementException("Provided linkElement was null");
        }
        jsClick(linkElement);
    }

    // Clear text from an input field ✅
    public void clearExistingTextValue(WebElement element) {
        element.clear();
    }

    // Type text into an input field ✅
    public void type(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    // Type text and press Enter in an input field ✅
    public void typeAndPressEnter(WebElement element, String text) {
        element.clear();
        element.sendKeys(text + Keys.ENTER);
    }

    // Select from dropdown using visible text ✅
    public void selectByVisibleText(WebElement element, String text) {
        new Select(element).selectByVisibleText(text);
    }

    // Select by value ✅
    public void selectByValue(WebElement element, String value) {
        new Select(element).selectByValue(value);
    }

    // Select by index ✅
    public void selectByIndex(WebElement element, int index) {
        new Select(element).selectByIndex(index);
    }

    // Hover over an element ✅
    public void hover(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    // Drag and drop from source to target element ✅
    public void dragAndDrop(WebElement source, WebElement target) {
        Actions actions = new Actions(driver);
        actions.dragAndDrop(source, target).perform();
    }

    // Drag and drop by offset ✅
    public void dragAndDropByOffset(WebElement source, int xOffset, int yOffset) {
        Actions actions = new Actions(driver);
        actions.dragAndDropBy(source, xOffset, yOffset).perform();
    }

    // Upload file to input element ✅
    public void uploadFile(WebElement fileInput, String filePath) {
        fileInput.sendKeys(filePath);
    }

}
