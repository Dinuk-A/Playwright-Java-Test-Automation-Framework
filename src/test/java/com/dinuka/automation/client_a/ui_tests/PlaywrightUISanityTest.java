
package com.dinuka.automation.client_a.ui_tests;

import com.dinuka.automation.utils.common.AllureLogger;
import com.dinuka.automation.utils.ui.core.PlaywrightBaseTest;
import com.dinuka.automation.utils.ui.playwright.AllureEvidenceLogger;
import com.dinuka.automation.utils.ui.playwright.ElementActions;
import com.dinuka.automation.utils.ui.playwright.ScreenCaptureUtils;
import com.dinuka.automation.utils.ui.playwright.UiAssertionUtils;
import com.dinuka.automation.utils.ui.playwright.WaitUtils;

import com.microsoft.playwright.Locator;

import io.qameta.allure.Allure;
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
import java.util.LinkedHashMap;
import java.util.Map;

@Epic("Framework Sanity Checks")
public class PlaywrightUISanityTest extends PlaywrightBaseTest {

        private static final String BASE_URL = "https://the-internet.herokuapp.com";

        // ========================================================================
        // Test Evidence Lifecycle
        // ========================================================================

        @BeforeMethod(alwaysRun = true)
        public void resetEvidence() {
                AllureEvidenceLogger.resetLog();
        }

        // ========================================================================
        // Evidence Helpers
        // ========================================================================

        /**
         * Captures a screenshot, saves it locally and attaches it directly
         * to the currently active Allure test or step.
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
         * Attaches custom test values directly to the active Allure step.
         */
        private void attachTestDetails(
                        String attachmentName,
                        Map<String, Object> details) {

                AllureLogger.logCustomFields(attachmentName, details);
        }

        /**
         * Creates an ordered map of evidence values.
         *
         * Usage:
         * details("Expected", "Hello", "Actual", actualText)
         */
        private Map<String, Object> details(Object... values) {

                Map<String, Object> result = new LinkedHashMap<>();

                for (int i = 0; i < values.length - 1; i += 2) {
                        result.put(String.valueOf(values[i]), values[i + 1]);
                }

                return result;
        }

        /**
         * Executes an action inside an Allure step.
         *
         * Action details and screenshots are attached inside the step,
         * even if the action fails.
         */
        private void executeStepWithEvidence(
                        String stepName,
                        Map<String, Object> stepDetails,
                        Runnable action) {

                Allure.step(stepName, () -> {

                        try {

                                action.run();

                        } finally {

                                attachTestDetails(
                                                stepName + " - Step Details",
                                                stepDetails);

                                captureAndAttachScreenshot(
                                                stepName + " - Screenshot");
                        }
                });
        }

        /**
         * Executes an assertion and records its result in AllureEvidenceLogger.
         *
         * Also attaches assertion values and a screenshot directly inside
         * the corresponding Allure step.
         *
         * The finally block ensures evidence is attached even when
         * the assertion fails.
         */
        private void assertWithEvidence(
                        String stepName,
                        Map<String, Object> assertionDetails,
                        Runnable assertion) {

                Allure.step(stepName + " - Evidence", () -> {

                        try {

                                AllureEvidenceLogger.assertStep(
                                                stepName,
                                                page,
                                                assertion);

                        } finally {

                                attachTestDetails(
                                                stepName + " - Assertion Values",
                                                assertionDetails);

                                captureAndAttachScreenshot(
                                                stepName + " - Screenshot");
                        }
                });
        }

        // ========================================================================
        // Test 1: Home Page Navigation & Assertions
        // ========================================================================

        @Test(groups = { "sanity" })
        @Story("Playwright UI - Navigation & Assertions")
        @Description("Verify Playwright can launch a browser, navigate, and read page content")
        @Severity(SeverityLevel.BLOCKER)
        public void testHomePageLoadsAndLinksArePresent() {

                executeStepWithEvidence(
                                "Navigate to Home Page",
                                details(
                                                "Target URL", BASE_URL),
                                () -> {
                                        page.navigate(BASE_URL);
                                        WaitUtils.waitForPageLoad(page);
                                });

                // Collect actual values.
                String actualTitle = page.title();

                String actualHeading = page.locator("h1").innerText();

                int linkCount = page.locator("#content ul li a").count();

                String currentUrl = page.url();

                // Attach overall test information.
                attachTestDetails(
                                "Playwright - Home Page Test Details",
                                details(
                                                "Expected Page Title", "The Internet",
                                                "Actual Page Title", actualTitle,
                                                "Expected Heading", "Welcome to the-internet",
                                                "Actual Heading", actualHeading,
                                                "Example Link Count", linkCount,
                                                "Current URL", currentUrl));

                // Assertion 1: Page title.
                assertWithEvidence(
                                "Verify Page Title",
                                details(
                                                "Expected Title", "The Internet",
                                                "Actual Title", actualTitle),
                                () -> UiAssertionUtils.assertPageTitle(
                                                page, "The Internet"));

                Locator heading = page.locator("h1");

                // Assertion 2: Heading visibility.
                assertWithEvidence(
                                "Verify Heading Visibility",
                                details(
                                                "Expected Visible", true,
                                                "Actual Visible", heading.isVisible()),
                                () -> UiAssertionUtils.assertElementVisible(heading));

                // Assertion 3: Heading text.
                assertWithEvidence(
                                "Verify Heading Text",
                                details(
                                                "Expected Text", "Welcome to the-internet",
                                                "Actual Text", actualHeading),
                                () -> UiAssertionUtils.assertElementContainsText(
                                                heading, "Welcome to the-internet"));

                // Assertion 4: Example links.
                assertWithEvidence(
                                "Verify Example Links",
                                details(
                                                "Expected Minimum Link Count", 1,
                                                "Actual Link Count", linkCount),
                                () -> Assert.assertTrue(
                                                linkCount > 0,
                                                "Expected the example-links list to contain at least one link"));

                AllureLogger.logText(
                                "Playwright - Home Page Result",
                                "All home page assertions completed successfully.");
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

                executeStepWithEvidence(
                                "Navigate to Login Page",
                                details(
                                                "Target URL", BASE_URL + "/login"),
                                () -> {
                                        page.navigate(BASE_URL + "/login");
                                        WaitUtils.waitForPageLoad(page);
                                });

                // Enter login credentials.
                executeStepWithEvidence(
                                "Enter Login Credentials",
                                details(
                                                "Username", "tomsmith",
                                                "Password", "[REDACTED]",
                                                "Input Method", "ElementActions.type()"),
                                () -> {
                                        actions.type(
                                                        page.locator("#username"), "tomsmith");

                                        actions.type(
                                                        page.locator("#password"),
                                                        "SuperSecretPassword!");
                                });

                actions.click(page.locator("button[type='submit']"));

                executeStepWithEvidence(
                                "Wait for Login Response",
                                details(
                                                "Expected Element", "#flash",
                                                "Wait Strategy", "Element visibility"),
                                () -> WaitUtils.waitForElementVisible(
                                                page.locator("#flash")));

                Locator flash = page.locator("#flash");

                String actualMessage = flash.innerText();

                String actualUrl = page.url();

                // Attach overall test information.
                attachTestDetails(
                                "Playwright - Login Test Details",
                                details(
                                                "Expected Login Message",
                                                "You logged into a secure area!",
                                                "Actual Login Message", actualMessage,
                                                "Expected URL Contains", "/secure",
                                                "Actual URL", actualUrl));

                // Assertion 1: Login confirmation message.
                assertWithEvidence(
                                "Verify Login Confirmation Message",
                                details(
                                                "Expected Message",
                                                "You logged into a secure area!",
                                                "Actual Message", actualMessage),
                                () -> UiAssertionUtils.assertElementContainsText(
                                                flash, "You logged into a secure area!"));

                // Assertion 2: Secure area URL.
                assertWithEvidence(
                                "Verify Secure Area Navigation",
                                details(
                                                "Expected URL Contains", "/secure",
                                                "Actual URL", actualUrl),
                                () -> UiAssertionUtils.assertUrlContains(
                                                page, "/secure"));

                AllureLogger.logText(
                                "Playwright - Login Result",
                                "Login confirmation and secure-area navigation verified.");
        }

        // ========================================================================
        // Test 3: Dynamic Loading & Waits
        // ========================================================================

        @Test(groups = { "sanity" })
        @Story("Playwright UI - Dynamic Waits")
        @Description("Verify WaitUtils correctly waits for an element that only appears after an async delay")
        @Severity(SeverityLevel.CRITICAL)
        public void testDynamicLoadingRevealsHiddenText() {

                executeStepWithEvidence(
                                "Navigate to Dynamic Loading Page",
                                details(
                                                "Target URL", BASE_URL + "/dynamic_loading/1"),
                                () -> {
                                        page.navigate(BASE_URL + "/dynamic_loading/1");
                                        WaitUtils.waitForPageLoad(page);
                                });

                Locator startButton = page.locator("#start button");

                // Assertion 1: Start button visibility.
                assertWithEvidence(
                                "Verify Start Button Visibility",
                                details(
                                                "Expected Visible", true,
                                                "Actual Visible", startButton.isVisible()),
                                () -> UiAssertionUtils.assertElementVisible(startButton));

                // Trigger asynchronous loading.
                executeStepWithEvidence(
                                "Start Dynamic Loading",
                                details(
                                                "Action", "Click Start button",
                                                "Expected Result", "Loading indicator appears"),
                                () -> startButton.click());

                // Wait for dynamically loaded content.
                Locator finishText = WaitUtils.waitForElementVisible(
                                page.locator("#finish h4"), 15000);

                String actualText = finishText.innerText();

                boolean isVisible = finishText.isVisible();

                // Attach overall test information.
                attachTestDetails(
                                "Playwright - Dynamic Loading Test Details",
                                details(
                                                "Expected Text", "Hello World!",
                                                "Actual Text", actualText,
                                                "Element Visible", isVisible));

                // Assertion 2: Dynamically loaded text.
                assertWithEvidence(
                                "Verify Dynamically Loaded Text",
                                details(
                                                "Expected Text", "Hello World!",
                                                "Actual Text", actualText,
                                                "Element Visible", isVisible),
                                () -> UiAssertionUtils.assertElementTextEquals(
                                                finishText, "Hello World!"));

                AllureLogger.logText(
                                "Playwright - Dynamic Loading Result",
                                "The delayed text appeared and matched the expected value.");
        }

        // ========================================================================
        // Final Test Evidence
        // ========================================================================

        @AfterMethod(alwaysRun = true)
        public void attachFinalTestEvidence(ITestResult result) {

                try {

                        if (page != null) {

                                if (result.getStatus() == ITestResult.FAILURE) {

                                        AllureLogger.logError(
                                                        "Playwright - Test Failure",
                                                        result.getThrowable());
                                }

                                // Final screenshot attached directly to the test.
                                captureAndAttachScreenshot(
                                                "Playwright - Final Test State");
                        }

                } catch (Exception e) {

                        AllureLogger.logText(
                                        "Playwright - Evidence Collection Warning",
                                        e.getMessage());

                } finally {

                        try {
                                // Supplementary HTML assertion summary.
                                AllureEvidenceLogger.attachSummary();
                        } finally {
                                AllureEvidenceLogger.clearThreadLocal();
                        }
                }
        }
}
