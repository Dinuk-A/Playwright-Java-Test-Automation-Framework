package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

import com.microsoft.playwright.TimeoutError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitUtils {

    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);

    private WaitUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ========================================================================
    // FOR VISIBILITY
    // ========================================================================

    /*
     * Wait for an element to become visible (in viewport and not hidden).
     * Returns the Locator after it becomes visible.
     */
    public static Locator waitForElementVisible(Locator locator, int timeoutInMillis) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setTimeout(timeoutInMillis)
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            return locator;
        } catch (PlaywrightException e) {
            throw new PlaywrightException(
                    "Element did not become visible within " + timeoutInMillis + "ms: " + e.getMessage());
        }
    }

    /*
     * Default timeout version (uses 10 seconds = 10000ms)
     */
    public static Locator waitForElementVisible(Locator locator) {
        return waitForElementVisible(locator, 10000);
    }

    // ========================================================================
    // FOR CLICKABLE
    // ========================================================================

    /*
     * Wait for an element to become clickable (visible and enabled).
     * Returns the Locator after it becomes clickable.
     */
    public static Locator waitForElementClickable(Locator locator, int timeoutInMillis) {
        try {
            // Playwright checks for visibility and enabled state automatically
            locator.waitFor(new Locator.WaitForOptions()
                    .setTimeout(timeoutInMillis)
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));

            // Verify it's enabled
            if (!locator.isEnabled()) {
                throw new PlaywrightException("Element is visible but not enabled");
            }
            return locator;
        } catch (PlaywrightException e) {
            throw new PlaywrightException(
                    "Element did not become clickable within " + timeoutInMillis + "ms: " + e.getMessage());
        }
    }

    /*
     * Default timeout version (uses 10 seconds = 10000ms)
     */
    public static Locator waitForElementClickable(Locator locator) {
        return waitForElementClickable(locator, 10000);
    }

    // ========================================================================
    // FOR PRESENCE (element exists in DOM, not necessarily visible)
    // ========================================================================

    /*
     * Wait for an element to be present in the DOM (exists but may be hidden).
     * Returns the Locator after it's present.
     */
    public static Locator waitForElementPresence(Locator locator, int timeoutInMillis) {
        try {
            // Check if at least one element matches the locator
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                if (locator.count() > 0) {
                    return locator;
                }
                Thread.sleep(100); // Poll every 100ms
            }
            throw new PlaywrightException("Element not found in DOM within " + timeoutInMillis + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout version (uses 10 seconds = 10000ms)
     */
    public static Locator waitForElementPresence(Locator locator) {
        return waitForElementPresence(locator, 10000);
    }

    // ========================================================================
    // TEXT CONDITIONS
    // ========================================================================

    /*
     * Wait for text to be present in an element.
     * Custom timeout version (timeoutInMillis).
     */
    public static boolean waitForTextInElement(Locator locator, String expectedText, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                String actualText = locator.textContent();
                if (actualText != null && actualText.contains(expectedText)) {
                    return true;
                }
                Thread.sleep(100); // Poll every 100ms
            }
            throw new PlaywrightException(
                    "Text '" + expectedText + "' not found in element within " + timeoutInMillis + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout version (uses 10 seconds = 10000ms)
     */
    public static boolean waitForTextInElement(Locator locator, String expectedText) {
        return waitForTextInElement(locator, expectedText, 10000);
    }

    /*
     * Wait for an element to have exact text.
     */
    public static boolean waitForElementTextEquals(Locator locator, String expectedText, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                String actualText = locator.textContent();
                if (actualText != null && actualText.trim().equals(expectedText.trim())) {
                    return true;
                }
                Thread.sleep(100);
            }
            throw new PlaywrightException(
                    "Element text did not equal '" + expectedText + "' within " + timeoutInMillis + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout version for exact text match
     */
    public static boolean waitForElementTextEquals(Locator locator, String expectedText) {
        return waitForElementTextEquals(locator, expectedText, 10000);
    }

    // ========================================================================
    // DIALOG/ALERT CONDITIONS
    // ========================================================================

    /*
     * Wait for a dialog/alert to appear.
     * Custom timeout version (timeoutInMillis).
     * Useful when you need to verify a dialog appears before handling it.
     */
    public static void waitForDialog(Page page, int timeoutInMillis) {
        try {
            java.util.concurrent.CompletableFuture<com.microsoft.playwright.Dialog> dialogFuture = new java.util.concurrent.CompletableFuture<>();
            page.onDialog(dialog -> dialogFuture.complete(dialog));
            dialogFuture.get(timeoutInMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            throw new PlaywrightException("Dialog did not appear within " + timeoutInMillis + "ms: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        } catch (Exception e) {
            throw new PlaywrightException("Failed while waiting for dialog: " + e.getMessage());
        }
    }

    /*
     * Default timeout version (uses 10 seconds = 10000ms)
     */
    public static void waitForDialog(Page page) {
        waitForDialog(page, 10000);
    }

    // ========================================================================
    // URL CONDITIONS
    // ========================================================================

    /*
     * Wait until the URL contains a specific string.
     * Custom timeout version (timeoutInMillis).
     */
    public static void waitForUrlContains(Page page, String partialUrl, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                if (page.url().contains(partialUrl)) {
                    return;
                }
                Thread.sleep(100);
            }
            throw new PlaywrightException("URL did not contain '" + partialUrl + "' within " + timeoutInMillis
                    + "ms. Current URL: " + page.url());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout version (uses 10 seconds = 10000ms)
     */
    public static void waitForUrlContains(Page page, String partialUrl) {
        waitForUrlContains(page, partialUrl, 10000);
    }

    /*
     * Wait until the URL exactly matches the expected URL.
     */
    public static void waitForUrlEquals(Page page, String expectedUrl, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                if (page.url().equals(expectedUrl)) {
                    return;
                }
                Thread.sleep(100);
            }
            throw new PlaywrightException("URL did not equal '" + expectedUrl + "' within " + timeoutInMillis
                    + "ms. Current URL: " + page.url());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout version for exact URL match
     */
    public static void waitForUrlEquals(Page page, String expectedUrl) {
        waitForUrlEquals(page, expectedUrl, 10000);
    }

    // ========================================================================
    // BONUS: Playwright-specific wait methods (more powerful than Selenium)
    // ========================================================================

    /*
     * Wait for an element to be hidden/not visible.
     */
    public static Locator waitForElementHidden(Locator locator, int timeoutInMillis) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setTimeout(timeoutInMillis)
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN));
            return locator;
        } catch (PlaywrightException e) {
            throw new PlaywrightException(
                    "Element did not become hidden within " + timeoutInMillis + "ms: " + e.getMessage());
        }
    }

    /*
     * Default timeout for hidden state
     */
    public static Locator waitForElementHidden(Locator locator) {
        return waitForElementHidden(locator, 10000);
    }

    /*
     * Wait for a page to load (useful after navigation).
     */
    public static void waitForPageLoad(Page page, int timeoutInMillis) {
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(timeoutInMillis));
        } catch (PlaywrightException e) {
            throw new PlaywrightException("Page did not load within " + timeoutInMillis + "ms: " + e.getMessage());
        }
    }

    /*
     * Default timeout for page load
     */
    public static void waitForPageLoad(Page page) {
        waitForPageLoad(page, 30000); // Default 30s for page load
    }

    /*
     * Wait for network idle (no pending requests/responses).
     * Useful after clicking a button that triggers API calls.
     */
    public static void waitForNetworkIdle(Page page, int timeoutInMillis) {
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE,
                    new Page.WaitForLoadStateOptions().setTimeout(timeoutInMillis));
        } catch (PlaywrightException e) {
            throw new PlaywrightException(
                    "Network did not become idle within " + timeoutInMillis + "ms: " + e.getMessage());
        }
    }

    public static void waitForPageFullyLoaded(Page page) {
        waitForPageFullyLoaded(page, 15000);
    }

    public static void waitForPageFullyLoaded(Page page, int timeoutInMillis) {

        // Stage 1a: browser "load" event
        try {
            waitForPageLoad(page, timeoutInMillis);
        } catch (PlaywrightException e) {
            logger.warn("Page 'load' event did not fire within {}ms before evidence capture.", timeoutInMillis);
        }

        // Stage 1b: network activity settling down
        try {
            waitForNetworkIdle(page, timeoutInMillis);
        } catch (PlaywrightException e) {
            logger.warn("Network did not go idle within {}ms before evidence capture; continuing anyway.",
                    timeoutInMillis);
        }

        // Stage 2: any app-level busy indicator (aria-busy="true") clearing
        try {
            page.waitForFunction(
                    "() => !document.querySelector('[aria-busy=\"true\"]')",
                    null,
                    new Page.WaitForFunctionOptions().setTimeout(timeoutInMillis));
        } catch (TimeoutError e) {
            logger.warn("An aria-busy=\"true\" indicator did not clear within {}ms before evidence capture.",
                    timeoutInMillis);
        }

        // Stage 3: trigger any images that only start loading once scrolled into view
        try {
            page.evaluate("() => window.scrollTo(0, document.body.scrollHeight)");
            page.waitForTimeout(200);
            page.evaluate("() => window.scrollTo(0, 0)");
        } catch (PlaywrightException e) {
            logger.warn("Could not scroll page to trigger lazy-loaded images: {}", e.getMessage());
        }

        // Stage 4: every <img> on the page finishing loading
        try {
            page.waitForFunction(
                    "() => Array.from(document.images).every(img => img.complete)",
                    null,
                    new Page.WaitForFunctionOptions().setTimeout(timeoutInMillis));
        } catch (TimeoutError e) {
            logger.warn("Not all <img> elements finished loading within {}ms before evidence capture.",
                    timeoutInMillis);
        }

        // Stage 5: brief settle buffer for any CSS transition/animation triggered above
        page.waitForTimeout(300);
    }

    /*
     * Default timeout for network idle
     */
    public static void waitForNetworkIdle(Page page) {
        waitForNetworkIdle(page, 30000); // Default 30s
    }

    /*
     * Wait for attribute to have a specific value.
     */
    public static boolean waitForAttributeValue(Locator locator, String attributeName,
            String expectedValue, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                String actualValue = locator.getAttribute(attributeName);
                if (actualValue != null && actualValue.equals(expectedValue)) {
                    return true;
                }
                Thread.sleep(100);
            }
            throw new PlaywrightException("Attribute '" + attributeName + "' did not equal '" + expectedValue
                    + "' within " + timeoutInMillis + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout for attribute value wait
     */
    public static boolean waitForAttributeValue(Locator locator, String attributeName, String expectedValue) {
        return waitForAttributeValue(locator, attributeName, expectedValue, 10000);
    }

    /*
     * Wait for element to have a specific CSS class.
     */
    public static boolean waitForElementHasClass(Locator locator, String className, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                String classes = locator.getAttribute("class");
                if (classes != null && classes.contains(className)) {
                    return true;
                }
                Thread.sleep(100);
            }
            throw new PlaywrightException(
                    "Element did not have class '" + className + "' within " + timeoutInMillis + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout for class wait
     */
    public static boolean waitForElementHasClass(Locator locator, String className) {
        return waitForElementHasClass(locator, className, 10000);
    }

    /*
     * Wait for element to be checked (checkbox/radio).
     */
    public static boolean waitForElementChecked(Locator locator, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                if (locator.isChecked()) {
                    return true;
                }
                Thread.sleep(100);
            }
            throw new PlaywrightException("Element did not become checked within " + timeoutInMillis + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout for checked state
     */
    public static boolean waitForElementChecked(Locator locator) {
        return waitForElementChecked(locator, 10000);
    }

    /*
     * Wait for element count to match expected count.
     * Useful for waiting for list items to load.
     */
    public static void waitForElementCount(Locator locator, int expectedCount, int timeoutInMillis) {
        try {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutInMillis) {
                if (locator.count() == expectedCount) {
                    return;
                }
                Thread.sleep(100);
            }
            throw new PlaywrightException("Element count did not reach " + expectedCount + " within " + timeoutInMillis
                    + "ms. Current count: " + locator.count());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlaywrightException("Wait interrupted: " + e.getMessage());
        }
    }

    /*
     * Default timeout for element count
     */
    public static void waitForElementCount(Locator locator, int expectedCount) {
        waitForElementCount(locator, expectedCount, 10000);
    }
}