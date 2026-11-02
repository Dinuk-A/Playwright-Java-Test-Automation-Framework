
package com.dinuka.automation.client_a.ui_tests;

import com.dinuka.automation.utils.common.AllureLogger;
import com.dinuka.automation.utils.ui.core.PlaywrightBaseTest;
import com.dinuka.automation.utils.ui.playwright.AllureEvidenceLogger;
import com.dinuka.automation.utils.ui.playwright.ElementActions;
import com.dinuka.automation.utils.ui.playwright.ScreenCaptureUtils;
import com.dinuka.automation.utils.ui.playwright.UiAssertionUtils;
import com.dinuka.automation.utils.ui.playwright.WaitUtils;

import com.microsoft.playwright.Locator;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Epic("Framework Sanity Checks")
public class PlaywrightUISanityTest extends PlaywrightBaseTest {

        private static final String BASE_URL = "https://the-internet.herokuapp.com";

        // ========================================================================
        // Test Evidence Lifecycle
        // ========================================================================

        /**
         * Resets assertion evidence before each test.
         */
        @BeforeMethod(alwaysRun = true)
        public void resetEvidence() {
                AllureEvidenceLogger.resetLog();
        }

        /**
         * Captures a screenshot, saves it locally and attaches it to Allure.
         */
        private void captureAndAttachScreenshot(String name) {

                if (page == null) {
                        return;
                }

                String screenshotPath = ScreenCaptureUtils.takePageScreenshot(page, name);

                if (screenshotPath != null && !screenshotPath.isBlank()) {

                        try {

                                byte[] screenshot = Files.readAllBytes(
                                                Paths.get(screenshotPath));

                                AllureLogger.logScreenshot(name, screenshot);

                        } catch (Exception e) {

                                AllureLogger.logText(
                                                "Screenshot Attachment Error",
                                                "Failed to attach screenshot: " + e.getMessage());
                        }
                }
        }

        /**
         * Attaches custom test values to Allure.
         */
        private void attachTestDetails(
                        String attachmentName,
                        Map<String, Object> details) {

                AllureLogger.logCustomFields(attachmentName, details);
        }

        // ========================================================================
        // Test 1: Home Page Navigation & Assertions
        // ========================================================================

        @Test(groups = { "sanity" })
        @Story("Playwright UI - Navigation & Assertions")
        @Description("Verify Playwright can launch a browser, navigate, and read page content")
        @Severity(SeverityLevel.BLOCKER)
        public void testHomePageLoadsAndLinksArePresent() {

                page.navigate(BASE_URL);

                WaitUtils.waitForPageLoad(page);

                captureAndAttachScreenshot(
                                "Playwright - Home Page Loaded");

                // Collect actual values.
                String actualTitle = page.title();

                String actualHeading = page.locator("h1").innerText();

                int linkCount = page.locator("#content ul li a").count();

                // Attach expected and actual values.
                Map<String, Object> details = new HashMap<>();

                details.put("Expected Page Title", "The Internet");
                details.put("Actual Page Title", actualTitle);

                details.put("Expected Heading", "Welcome to the-internet");
                details.put("Actual Heading", actualHeading);

                details.put("Example Link Count", linkCount);
                details.put("Current URL", page.url());

                attachTestDetails(
                                "Playwright - Home Page Test Details", details);

                // Assertions with individual Allure evidence.
                AllureEvidenceLogger.assertStep(
                                "Verify page title",
                                page,
                                () -> UiAssertionUtils.assertPageTitle(
                                                page, "The Internet"));

                Locator heading = page.locator("h1");

                AllureEvidenceLogger.assertStep(
                                "Verify heading is visible",
                                page,
                                () -> UiAssertionUtils.assertElementVisible(heading));

                AllureEvidenceLogger.assertStep(
                                "Verify heading contains expected text",
                                page,
                                () -> UiAssertionUtils.assertElementContainsText(
                                                heading, "Welcome to the-internet"));

                AllureEvidenceLogger.assertStep(
                                "Verify example links are present",
                                page,
                                () -> Assert.assertTrue(
                                                linkCount > 0,
                                                "Expected the example-links list to contain at least one link"));

                AllureLogger.logText(
                                "Playwright - Home Page Assertions",
                                "PASS: Page title, heading visibility, heading text and example links verified.");
        }

        // ========================================================================
        // Test 2: Form Authentication
        // ========================================================================

        @Test(groups = { "sanity" })
        @Story("Playwright UI - Form Input & Click")
        @Description("Verify ElementActions can type into fields, click, and the resulting page state is correct")
        @Severity(SeverityLevel.BLOCKER)
        public void testFormAuthenticationHappyPathLogsIn() {

                ElementActions actions = new ElementActions(page);

                page.navigate(BASE_URL + "/login");

                WaitUtils.waitForPageLoad(page);

                captureAndAttachScreenshot(
                                "Playwright - Login Page");

                // Enter login credentials.
                actions.type(page.locator("#username"), "tomsmith");

                actions.type(
                                page.locator("#password"),
                                "SuperSecretPassword!");

                // Never attach or log the actual password.
                AllureLogger.logText(
                                "Playwright - Login Input",
                                "Username entered: tomsmith\nPassword entered: [REDACTED]");

                actions.click(page.locator("button[type='submit']"));

                WaitUtils.waitForElementVisible(
                                page.locator("#flash"));

                Locator flash = page.locator("#flash");

                String actualMessage = flash.innerText();

                String actualUrl = page.url();

                // Attach expected and actual values.
                Map<String, Object> details = new HashMap<>();

                details.put("Expected Login Message",
                                "You logged into a secure area!");

                details.put("Actual Login Message", actualMessage);

                details.put("Expected URL Contains", "/secure");
                details.put("Actual URL", actualUrl);

                attachTestDetails(
                                "Playwright - Login Test Details", details);

                captureAndAttachScreenshot(
                                "Playwright - Login Successful");

                // Assertions with individual Allure evidence.
                AllureEvidenceLogger.assertStep(
                                "Verify successful login confirmation message",
                                page,
                                () -> UiAssertionUtils.assertElementContainsText(
                                                flash, "You logged into a secure area!"));

                AllureEvidenceLogger.assertStep(
                                "Verify navigation to secure area",
                                page,
                                () -> UiAssertionUtils.assertUrlContains(
                                                page, "/secure"));

                AllureLogger.logText(
                                "Playwright - Login Assertions",
                                "PASS: Login confirmation message and secure-area URL verified.");
        }

        // ========================================================================
        // Test 3: Dynamic Loading & Waits
        // ========================================================================

        @Test(groups = { "sanity" })
        @Story("Playwright UI - Dynamic Waits")
        @Description("Verify WaitUtils correctly waits for an element that only appears after an async delay")
        @Severity(SeverityLevel.CRITICAL)
        public void testDynamicLoadingRevealsHiddenText() {

                page.navigate(BASE_URL + "/dynamic_loading/1");

                WaitUtils.waitForPageLoad(page);

                Locator startButton = page.locator("#start button");

                AllureEvidenceLogger.assertStep(
                                "Verify start button is visible",
                                page,
                                () -> UiAssertionUtils.assertElementVisible(startButton));

                captureAndAttachScreenshot(
                                "Playwright - Dynamic Loading Before Start");

                // Start asynchronous loading.
                startButton.click();

                Locator finishText = WaitUtils.waitForElementVisible(
                                page.locator("#finish h4"), 15000);

                String actualText = finishText.innerText();

                boolean isVisible = finishText.isVisible();

                // Attach expected and actual values.
                Map<String, Object> details = new HashMap<>();

                details.put("Expected Text", "Hello World!");
                details.put("Actual Text", actualText);
                details.put("Element Visible", isVisible);

                attachTestDetails(
                                "Playwright - Dynamic Loading Test Details", details);

                captureAndAttachScreenshot(
                                "Playwright - Dynamic Loading Completed");

                // Assertion with individual Allure evidence.
                AllureEvidenceLogger.assertStep(
                                "Verify dynamically loaded text",
                                page,
                                () -> UiAssertionUtils.assertElementTextEquals(
                                                finishText, "Hello World!"));

                AllureLogger.logText(
                                "Playwright - Dynamic Loading Assertion",
                                "PASS: The delayed text appeared and matched the expected value.");
        }

        // ========================================================================
        // Final Test Evidence
        // ========================================================================

        /**
         * Captures final screenshot and failure details before browser teardown.
         * Attaches the assertion summary even if screenshot collection fails.
         */
        @AfterMethod(alwaysRun = true)
        public void attachFinalTestEvidence(ITestResult result) {

                try {

                        if (page != null) {

                                // Attach failure details when a test fails.
                                if (result.getStatus() == ITestResult.FAILURE) {

                                        AllureLogger.logError(
                                                        "Playwright - Test Failure",
                                                        result.getThrowable());
                                }

                                // Capture final browser state.
                                captureAndAttachScreenshot(
                                                "Playwright - Final Test State");
                        }

                } catch (Exception e) {

                        AllureLogger.logText(
                                        "Playwright - Evidence Collection Warning",
                                        e.getMessage());

                } finally {

                        // Attach chronological assertion evidence table.
                        AllureEvidenceLogger.attachSummary();
                }
        }
}
