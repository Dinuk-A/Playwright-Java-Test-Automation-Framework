package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

public class IframeActions {

    private Page page;

    public IframeActions(Page page) {
        this.page = page;
    }

    /*
     * Switch to an iframe by its index.
     * Returns a FrameLocator that you can use to interact with elements inside the
     * iframe.
     */
    public FrameLocator switchToFrameByIndex(int index) {
        try {
            // Get all iframes and select by index
            Locator iframes = page.locator("iframe");
            if (index < iframes.count()) {
                // frameLocator() expects a selector, so we use nth-child for index-based access
                return page.frameLocator("iframe:nth-child(" + (index + 1) + ")");
            }
            throw new PlaywrightException("Frame index " + index + " not found");
        } catch (Exception e) {
            throw new PlaywrightException("Error switching to frame by index: " + e.getMessage());
        }
    }

    /*
     * Switch to an iframe by its name or ID attribute.
     * Returns a FrameLocator for interacting with elements inside the iframe.
     */
    public FrameLocator switchToFrameByNameOrId(String nameOrId) {
        try {
            // Try by ID first
            String idSelector = "iframe#" + nameOrId;
            if (isFramePresent(idSelector)) {
                return page.frameLocator(idSelector);
            }

            // Try by name attribute
            String nameSelector = "iframe[name='" + nameOrId + "']";
            if (isFramePresent(nameSelector)) {
                return page.frameLocator(nameSelector);
            }

            throw new PlaywrightException("Frame with name or ID '" + nameOrId + "' not found");
        } catch (Exception e) {
            throw new PlaywrightException("Error switching to frame by name/ID: " + e.getMessage());
        }
    }

    /*
     * Switch to an iframe using a Locator reference (element within the page).
     * Returns a FrameLocator for the identified iframe.
     */
    public FrameLocator switchToFrameByLocator(Locator frameLocator) {
        try {
            // Get the selector from the locator and create frameLocator
            String selector = frameLocator.toString();
            return page.frameLocator(selector);
        } catch (Exception e) {
            throw new PlaywrightException("Error switching to frame by locator: " + e.getMessage());
        }
    }

    /*
     * Switch back to the main page context (exit from iframe).
     * After calling this, you work directly with the page again.
     */
    public void switchToDefaultContent() {
        // In Playwright, you simply stop using the FrameLocator and go back to using
        // page directly
        // This is implicit—just use 'page' instead of the FrameLocator
        // This method exists for API parity with Selenium
    }

    /*
     * Switch to the parent frame (for nested iframes).
     * Note: Playwright doesn't have direct parentFrame() support like Selenium.
     * You need to keep track of your frame hierarchy manually.
     */
    public void switchToParentFrame() {
        // Playwright doesn't have built-in parent frame navigation
        // You must manage this manually by returning to page or tracking your frame
        // stack
        throw new UnsupportedOperationException(
                "Playwright doesn't support direct parent frame navigation. " +
                        "Use switchToDefaultContent() or maintain your own frame stack.");
    }

    /*
     * Check if a frame exists by CSS selector.
     * Returns true if the frame is found on the page.
     */
    public boolean isFramePresent(String selector) {
        try {
            return page.locator(selector).count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
