package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.MouseButton;
import java.nio.file.Path;

public class ElementActions {

    private Page page;

    // Constructor to initialize the Page object
    public ElementActions(Page page) {
        this.page = page;
    }

    // Click on an element - Playwright handles retries automatically
    public void click(Locator element) {
        element.click();
    }

    // JavaScript click on an element
    public void jsClick(Locator element) {
        page.evaluate("el => el.click()", element.elementHandle());
    }

    // Double click on an element
    public void doubleClick(Locator element) {
        element.dblclick();
    }

    // Right click on an element
    public void rightClick(Locator element) {
        element.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
    }

    // Click on a link element
    public void clickLink(Locator linkElement) {
        if (linkElement == null) {
            throw new IllegalArgumentException("Provided linkElement was null");
        }
        linkElement.click();
    }

    // Clear text from an input field
    public void clearExistingTextValue(Locator element) {
        element.clear();
    }

    // Type text into an input field
    public void type(Locator element, String text) {
        element.clear();
        element.fill(text);
    }

    // Type text and press Enter
    public void typeAndPressEnter(Locator element, String text) {
        element.clear();
        element.fill(text);
        element.press("Enter");
    }

    // Select from dropdown using visible text
    public void selectByVisibleText(Locator element, String text) {
        element.selectOption(text);
    }

    // Select by value attribute
    public void selectByValue(Locator element, String value) {
        element.selectOption(new String[] { value });
    }

    // Select by index (0-based)
    public void selectByIndex(Locator element, int index) {
        String[] options = element.locator("option").allTextContents()
                .toArray(new String[0]);
        if (index < options.length) {
            element.selectOption(options[index]);
        }
    }

    // Hover over an element
    public void hover(Locator element) {
        element.hover();
    }

    // Drag and drop from source to target element
    public void dragAndDrop(Locator source, Locator target) {
        source.dragTo(target);
    }

    // Drag and drop by offset (in pixels)
    public void dragAndDropByOffset(Locator source, int xOffset, int yOffset) {
        source.hover();
        page.mouse().down();
        page.mouse().move(xOffset, yOffset);
        page.mouse().up();
    }

    // Upload file to input element
    // Playwright handles file uploads differently—pass the file path directly
    public void uploadFile(Locator fileInput, String filePath) {
        fileInput.setInputFiles(Path.of(filePath));
    }

}
