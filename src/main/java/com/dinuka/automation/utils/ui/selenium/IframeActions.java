package com.dinuka.automation.utils.ui.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class IframeActions {
    private WebDriver driver;

    public IframeActions(WebDriver driver) {
        this.driver = driver;
    }

    // Switch to an iframe by its index.
    public void switchToFrameByIndex(int index) {
        driver.switchTo().frame(index);
    }

    // Switch to an iframe by its name or ID.
    public void switchToFrameByNameOrId(String nameOrId) {
        driver.switchTo().frame(nameOrId);
    }

    // Switch to an iframe using a WebElement reference.
    public void switchToFrameByElement(WebElement frameElement) {
        driver.switchTo().frame(frameElement);
    }

    // Switch back to the default content (main document) from an iframe.
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // Switch to the parent frame (useful for nested iframes).
    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
    }

    // Check if a frame exists by locator (returns true if found).
    public boolean isFramePresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
