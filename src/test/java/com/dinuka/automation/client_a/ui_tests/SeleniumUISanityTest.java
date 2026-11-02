
package com.dinuka.automation.client_a.ui_tests;

import com.dinuka.automation.utils.common.AllureLogger;
import com.dinuka.automation.utils.ui.core.SeleniumBaseTest;
import com.dinuka.automation.utils.ui.selenium.AllureEvidenceLogger;
import com.dinuka.automation.utils.ui.selenium.BrowserUtils;
import com.dinuka.automation.utils.ui.selenium.ElementActions;
import com.dinuka.automation.utils.ui.selenium.ScreenCaptureUtils;
import com.dinuka.automation.utils.ui.selenium.UiAssertionUtils;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
public class SeleniumUISanityTest extends SeleniumBaseTest {

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

                if (driver == null) {
                        return;
                }

                String screenshotPath = ScreenCaptureUtils.takePageScreenshot(driver, name);

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
         */
        private Map<String, Object> details(Object... values) {

                Map<String, Object> result = new LinkedHashMap<>();

                for (int i = 0; i < values.length - 1; i += 2) {
                        result.put(String.valueOf(values[i]), values[i + 1]);
                }

                return result;
        }

        /**
         * Executes an action inside an Allure step and attaches
         * action details and a screenshot.
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
         * Executes an assertion and records it in AllureEvidenceLogger.
         *
         * Assertion values and screenshots are also attached directly
         * inside the corresponding Allure step.
         */
        private void assertWithEvidence(
                        String stepName,
                        Map<String, Object> assertionDetails,
                        Runnable assertion) {

                Allure.step(stepName + " - Evidence", () -> {

                        try {

                                AllureEvidenceLogger.assertStep(
                                                stepName,
                                                driver,
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
        @Story("Selenium UI - Navigation & Assertions")
        @Description("Verify Selenium can launch a browser, navigate, and read page content")
        @Severity(SeverityLevel.BLOCKER)
        public void testHomePageLoadsAndLinksArePresent() {

                executeStepWithEvidence(
                                "Navigate to Home Page",
                                details(
                                                "Target URL", BASE_URL),
                                () -> {
                                        BrowserUtils.openUrl(driver, BASE_URL);

                                        wait.until(ExpectedConditions.visibilityOfElementLocated(
                                                        By.tagName("h1")));
                                });

                // Collect actual values.
                String actualTitle = driver.getTitle();

                WebElement heading = driver.findElement(By.tagName("h1"));

                String actualHeading = heading.getText();

                int linkCount = driver.findElements(
                                By.cssSelector("#content ul li a")).size();

                String currentUrl = driver.getCurrentUrl();

                // Attach overall test information.
                attachTestDetails(
                                "Selenium - Home Page Test Details",
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
                                                driver, "The Internet"));

                // Assertion 2: Heading visibility.
                assertWithEvidence(
                                "Verify Heading Visibility",
                                details(
                                                "Expected Visible", true,
                                                "Actual Visible", heading.isDisplayed()),
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
                                "Selenium - Home Page Result",
                                "All home page assertions completed successfully.");
        }

        // ========================================================================
        // Test 2: Form Authentication
        // ========================================================================

        @Test(groups = { "sanity" })
        @Story("Selenium UI - Form Input & Click")
        @Description("Verify ElementActions can type into fields, click, and the resulting page state is correct")
        @Severity(SeverityLevel.BLOCKER)
        public void testFormAuthenticationHappyPathLogsIn() {

                ElementActions actions = new ElementActions(driver);

                executeStepWithEvidence(
                                "Navigate to Login Page",
                                details(
                                                "Target URL", BASE_URL + "/login"),
                                () -> {
                                        BrowserUtils.openUrl(driver, BASE_URL + "/login");

                                        wait.until(ExpectedConditions.visibilityOfElementLocated(
                                                        By.id("username")));
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
                                                        driver.findElement(By.id("username")),
                                                        "tomsmith");

                                        actions.type(
                                                        driver.findElement(By.id("password")),
                                                        "SuperSecretPassword!");
                                });

                // Submit login form.
                executeStepWithEvidence(
                                "Submit Login Form",
                                details(
                                                "Action", "Click Login button",
                                                "Expected Result", "Successful authentication"),
                                () -> actions.click(driver.findElement(
                                                By.cssSelector("button[type='submit']"))));

                // Wait for login response.
                executeStepWithEvidence(
                                "Wait for Login Response",
                                details(
                                                "Expected Element", "#flash",
                                                "Wait Strategy", "Element visibility"),
                                () -> wait.until(
                                                ExpectedConditions.visibilityOfElementLocated(
                                                                By.id("flash"))));

                WebElement flash = driver.findElement(By.id("flash"));

                String actualMessage = flash.getText();

                String actualUrl = driver.getCurrentUrl();

                // Attach overall test information.
                attachTestDetails(
                                "Selenium - Login Test Details",
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
                                                driver, "/secure"));

                AllureLogger.logText(
                                "Selenium - Login Result",
                                "Login confirmation and secure-area navigation verified.");
        }

        // ========================================================================
        // Test 3: Checkbox Interaction
        // ========================================================================

        @Test(groups = { "sanity" })
        @Story("Selenium UI - Checkbox Interaction")
        @Description("Verify a checkbox can be toggled and its state changes correctly")
        @Severity(SeverityLevel.NORMAL)
        public void testCheckboxesCanBeToggled() {

                executeStepWithEvidence(
                                "Navigate to Checkbox Page",
                                details(
                                                "Target URL", BASE_URL + "/checkboxes"),
                                () -> BrowserUtils.openUrl(
                                                driver, BASE_URL + "/checkboxes"));

                WebElement firstCheckbox = driver.findElement(
                                By.cssSelector(
                                                "#checkboxes input[type='checkbox']:nth-of-type(1)"));

                boolean initialState = firstCheckbox.isSelected();

                // Attach initial checkbox state.
                attachTestDetails(
                                "Selenium - Checkbox Initial State",
                                details(
                                                "Checkbox Selected Before Click", initialState));

                // Click checkbox.
                executeStepWithEvidence(
                                "Toggle First Checkbox",
                                details(
                                                "Initial State", initialState,
                                                "Action", "Click first checkbox"),
                                firstCheckbox::click);

                boolean finalState = firstCheckbox.isSelected();

                // Attach final checkbox state.
                attachTestDetails(
                                "Selenium - Checkbox Final State",
                                details(
                                                "Checkbox Selected Before Click", initialState,
                                                "Checkbox Selected After Click", finalState,
                                                "Expected State Changed", true));

                // Assertion: Verify checkbox state changed.
                assertWithEvidence(
                                "Verify Checkbox State Changed",
                                details(
                                                "Expected Initial State", initialState,
                                                "Actual Final State", finalState,
                                                "Expected State Changed", true),
                                () -> Assert.assertNotEquals(
                                                finalState,
                                                initialState,
                                                "Expected the checkbox state to flip after clicking it"));

                AllureLogger.logText(
                                "Selenium - Checkbox Result",
                                "Checkbox state changed from "
                                                + initialState + " to " + finalState + ".");
        }

        // ========================================================================
        // Final Test Evidence
        // ========================================================================

        @AfterMethod(alwaysRun = true)
        public void attachFinalTestEvidence(ITestResult result) {

                try {

                        if (driver != null) {

                                if (result.getStatus() == ITestResult.FAILURE) {

                                        AllureLogger.logError(
                                                        "Selenium - Test Failure",
                                                        result.getThrowable());
                                }

                                // Final screenshot attached directly to the test.
                                captureAndAttachScreenshot(
                                                "Selenium - Final Test State");
                        }

                } catch (Exception e) {

                        AllureLogger.logText(
                                        "Selenium - Evidence Collection Warning",
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
