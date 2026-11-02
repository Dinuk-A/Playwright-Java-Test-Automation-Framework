
package com.dinuka.automation.client_a.ui_tests;

import com.dinuka.automation.utils.common.AllureLogger;
import com.dinuka.automation.utils.ui.core.SeleniumBaseTest;
import com.dinuka.automation.utils.ui.selenium.AllureEvidenceLogger;
import com.dinuka.automation.utils.ui.selenium.BrowserUtils;
import com.dinuka.automation.utils.ui.selenium.ElementActions;
import com.dinuka.automation.utils.ui.selenium.ScreenCaptureUtils;
import com.dinuka.automation.utils.ui.selenium.UiAssertionUtils;

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
import java.util.HashMap;
import java.util.Map;

public class SeleniumUISanityTest extends SeleniumBaseTest {

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
        public void testHomePageLoadsAndLinksArePresent() {

                BrowserUtils.openUrl(driver, BASE_URL);

                wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.tagName("h1")));

                captureAndAttachScreenshot(
                                "Selenium - Home Page Loaded");

                // Collect actual values.
                String actualTitle = driver.getTitle();

                WebElement heading = driver.findElement(By.tagName("h1"));

                String actualHeading = heading.getText();

                int linkCount = driver.findElements(
                                By.cssSelector("#content ul li a")).size();

                // Attach expected and actual values.
                Map<String, Object> details = new HashMap<>();

                details.put("Expected Page Title", "The Internet");
                details.put("Actual Page Title", actualTitle);

                details.put("Expected Heading", "Welcome to the-internet");
                details.put("Actual Heading", actualHeading);

                details.put("Example Link Count", linkCount);
                details.put("Current URL", driver.getCurrentUrl());

                attachTestDetails(
                                "Selenium - Home Page Test Details", details);

                // Assertions with individual Allure evidence.
                AllureEvidenceLogger.assertStep(
                                "Verify page title",
                                driver,
                                () -> UiAssertionUtils.assertPageTitle(
                                                driver, "The Internet"));

                AllureEvidenceLogger.assertStep(
                                "Verify heading is visible",
                                driver,
                                () -> UiAssertionUtils.assertElementVisible(heading));

                AllureEvidenceLogger.assertStep(
                                "Verify heading contains expected text",
                                driver,
                                () -> UiAssertionUtils.assertElementContainsText(
                                                heading, "Welcome to the-internet"));

                AllureEvidenceLogger.assertStep(
                                "Verify example links are present",
                                driver,
                                () -> Assert.assertTrue(
                                                linkCount > 0,
                                                "Expected the example-links list to contain at least one link"));

                AllureLogger.logText(
                                "Selenium - Home Page Assertions",
                                "PASS: Page title, heading visibility, heading text and example links verified.");
        }

        // ========================================================================
        // Test 2: Form Authentication
        // ========================================================================

        @Test(groups = { "sanity" })
        public void testFormAuthenticationHappyPathLogsIn() {

                ElementActions actions = new ElementActions(driver);

                BrowserUtils.openUrl(driver, BASE_URL + "/login");

                wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.id("username")));

                captureAndAttachScreenshot(
                                "Selenium - Login Page");

                // Enter login credentials.
                actions.type(
                                driver.findElement(By.id("username")),
                                "tomsmith");

                actions.type(
                                driver.findElement(By.id("password")),
                                "SuperSecretPassword!");

                // Never attach or log the actual password.
                AllureLogger.logText(
                                "Selenium - Login Input",
                                "Username entered: tomsmith\nPassword entered: [REDACTED]");

                actions.click(driver.findElement(
                                By.cssSelector("button[type='submit']")));

                wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.id("flash")));

                WebElement flash = driver.findElement(By.id("flash"));

                String actualMessage = flash.getText();

                String actualUrl = driver.getCurrentUrl();

                // Attach expected and actual values.
                Map<String, Object> details = new HashMap<>();

                details.put("Expected Login Message",
                                "You logged into a secure area!");

                details.put("Actual Login Message", actualMessage);

                details.put("Expected URL Contains", "/secure");
                details.put("Actual URL", actualUrl);

                attachTestDetails(
                                "Selenium - Login Test Details", details);

                captureAndAttachScreenshot(
                                "Selenium - Login Successful");

                // Assertions with individual Allure evidence.
                AllureEvidenceLogger.assertStep(
                                "Verify successful login confirmation message",
                                driver,
                                () -> UiAssertionUtils.assertElementContainsText(
                                                flash, "You logged into a secure area!"));

                AllureEvidenceLogger.assertStep(
                                "Verify navigation to secure area",
                                driver,
                                () -> UiAssertionUtils.assertUrlContains(
                                                driver, "/secure"));

                AllureLogger.logText(
                                "Selenium - Login Assertions",
                                "PASS: Login confirmation message and secure-area URL verified.");
        }

        // ========================================================================
        // Test 3: Checkbox Interaction
        // ========================================================================

        @Test(groups = { "sanity" })
        public void testCheckboxesCanBeToggled() {

                BrowserUtils.openUrl(driver, BASE_URL + "/checkboxes");

                WebElement firstCheckbox = driver.findElement(
                                By.cssSelector(
                                                "#checkboxes input[type='checkbox']:nth-of-type(1)"));

                boolean initialState = firstCheckbox.isSelected();

                // Attach initial state.
                Map<String, Object> initialDetails = new HashMap<>();

                initialDetails.put(
                                "Checkbox Selected Before Click", initialState);

                attachTestDetails(
                                "Selenium - Checkbox Initial State", initialDetails);

                captureAndAttachScreenshot(
                                "Selenium - Checkbox Before Click");

                // Click checkbox.
                firstCheckbox.click();

                boolean finalState = firstCheckbox.isSelected();

                // Attach final state.
                Map<String, Object> finalDetails = new HashMap<>();

                finalDetails.put(
                                "Checkbox Selected Before Click", initialState);

                finalDetails.put(
                                "Checkbox Selected After Click", finalState);

                finalDetails.put("Expected State Changed", true);

                attachTestDetails(
                                "Selenium - Checkbox Final State", finalDetails);

                captureAndAttachScreenshot(
                                "Selenium - Checkbox After Click");

                // Assertion with individual Allure evidence.
                AllureEvidenceLogger.assertStep(
                                "Verify checkbox state changes after clicking",
                                driver,
                                () -> Assert.assertNotEquals(
                                                finalState,
                                                initialState,
                                                "Expected the checkbox state to flip after clicking it"));

                AllureLogger.logText(
                                "Selenium - Checkbox Assertion",
                                "PASS: Checkbox state changed from "
                                                + initialState + " to " + finalState + ".");
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

                        if (driver != null) {

                                // Attach failure details when a test fails.
                                if (result.getStatus() == ITestResult.FAILURE) {

                                        AllureLogger.logError(
                                                        "Selenium - Test Failure",
                                                        result.getThrowable());
                                }

                                // Capture final browser state.
                                captureAndAttachScreenshot(
                                                "Selenium - Final Test State");
                        }

                } catch (Exception e) {

                        AllureLogger.logText(
                                        "Selenium - Evidence Collection Warning",
                                        e.getMessage());

                } finally {

                        // Attach chronological assertion evidence table.
                        AllureEvidenceLogger.attachSummary();
                }
        }
}
