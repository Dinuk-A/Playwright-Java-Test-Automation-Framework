package com.dinuka.automation.client_a.ui_tests;

import com.dinuka.automation.client_a.utils.ui_utils.PlaywrightSanityUtils;
import com.dinuka.automation.client_a.utils.ui_utils.PlaywrightSanityUtils.DialogResult;
import com.dinuka.automation.utils.common.ConfigPropertyReader;
import com.dinuka.automation.utils.ui.core.PlaywrightBaseTest;
import com.dinuka.automation.utils.ui.playwright.AllureEvidenceLogger;
import com.dinuka.automation.utils.ui.playwright.UiAssertionUtils;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

@Epic("Framework Sanity Checks")
@Feature("Playwright UI Sanity")
public class PlaywrightUISanityTest extends PlaywrightBaseTest {

        private static final String BASE_URL = ConfigPropertyReader.getProperty("sanity.ui.base.url");

        private PlaywrightSanityUtils utils;

        // ====================================================================
        // Small helpers for Allure (kept private so tests stay readable)
        // ====================================================================

        /** Attach plain text (test data, notes, extracted values) to the current step. */
        private void attachText(String name, String content) {
                Allure.addAttachment(name, "text/plain", content);
        }

        /** Take a screenshot of the current page and attach it as a PNG. */
        private void attachScreenshot(String name) {
                byte[] png = page.screenshot();
                Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), "png");
        }

        // ====================================================================
        // Lifecycle
        // ====================================================================

        @BeforeMethod(alwaysRun = true)
        public void setUpSanity() {
                AllureEvidenceLogger.resetLog();
                utils = new PlaywrightSanityUtils(page);
                utils.startDiagnostics();
        }

        @AfterMethod(alwaysRun = true)
        public void attachSanityEvidence(ITestResult result) {
                utils.attachFinalEvidence(result);
        }

        // ====================================================================
        // Data providers
        // ====================================================================

        // { label, username, password, expectedFlashContains, expectedUrlPart }
        @DataProvider(name = "loginScenarios")
        public Object[][] loginScenarios() {
                return new Object[][] {
                                { "valid credentials", "tomsmith", "SuperSecretPassword!",
                                                "You logged into a secure area!", "/secure" },
                                { "invalid username", "nobody", "SuperSecretPassword!",
                                                "Your username is invalid!", "/login" },
                                { "invalid password", "tomsmith", "wrong-password",
                                                "Your password is invalid!", "/login" }
                };
        }

        @DataProvider(name = "dynamicLoadingExamples")
        public Object[][] dynamicLoadingExamples() {
                return new Object[][] {
                                { "Example 1 - element hidden on page", "/dynamic_loading/1" },
                                { "Example 2 - element rendered after the fact", "/dynamic_loading/2" }
                };
        }

        // { value, expectedOptionText }
        @DataProvider(name = "dropdownOptions")
        public Object[][] dropdownOptions() {
                return new Object[][] { { "1", "Option 1" }, { "2", "Option 2" } };
        }

        // { label, triggerSelector, accept, promptText, expectedType, expectedMessage, expectedResult }
        @DataProvider(name = "dialogScenarios")
        public Object[][] dialogScenarios() {
                return new Object[][] {
                                { "alert", "button[onclick='jsAlert()']", true, null,
                                                "alert", "I am a JS Alert", "You successfully clicked an alert" },
                                { "confirm - accept", "button[onclick='jsConfirm()']", true, null,
                                                "confirm", "I am a JS Confirm", "You clicked: Ok" },
                                { "confirm - dismiss", "button[onclick='jsConfirm()']", false, null,
                                                "confirm", "I am a JS Confirm", "You clicked: Cancel" },
                                { "prompt - text entered", "button[onclick='jsPrompt()']", true, "Allure",
                                                "prompt", "I am a JS prompt", "You entered: Allure" }
                };
        }

        @DataProvider(name = "statusCodes")
        public Object[][] statusCodes() {
                return new Object[][] { { 200 }, { 404 }, { 500 } };
        }

        // ====================================================================
        // Home page
        // ====================================================================

        @Test(groups = { "sanity" })
        @Story("Navigation & Assertions")
        @Description("Verify Playwright can launch a browser, navigate, and read page content")
        @Severity(SeverityLevel.BLOCKER)
        public void testHomePageLoadsAndLinksArePresent() {
                utils.startTest("Home page loads and links are present");

                Allure.step("Open the home page", () -> {
                        utils.navigateTo(BASE_URL);
                        attachText("Opened URL", BASE_URL);
                        attachScreenshot("Home page after load");
                });

                Locator heading = page.locator("h1");
                Locator links = page.locator("#content ul li a");

                Allure.step("Verify page title and heading", () -> {
                        utils.verify("Page title", "The Internet", page::title,
                                        () -> UiAssertionUtils.assertPageTitle(page, "The Internet"));

                        utils.verify("Heading is visible", true, heading::isVisible,
                                        () -> UiAssertionUtils.assertElementVisible(heading));

                        utils.verify("Heading text", "contains 'Welcome to the-internet'", heading::innerText,
                                        () -> UiAssertionUtils.assertElementContainsText(heading,
                                                        "Welcome to the-internet"));
                });

                Allure.step("Verify the example link list", () -> {
                        // Record what we actually found so the report shows the real data
                        attachText("Example links found", String.valueOf(links.count()));

                        // Vacuous-truth guard
                        utils.verify("Example link list is not empty", "more than 0 links", links::count,
                                        () -> Assert.assertTrue(links.count() > 0,
                                                        "NOT EXERCISED: example link list is empty"));

                        Locator checkboxesLink = page.locator("#content a[href='/checkboxes']");
                        utils.verify("Checkboxes link target", "/checkboxes",
                                        () -> checkboxesLink.getAttribute("href"),
                                        () -> UiAssertionUtils.assertAttributeEquals(checkboxesLink, "href",
                                                        "/checkboxes"));
                });
        }

        // ====================================================================
        // Form authentication (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "loginScenarios")
        @Story("Form Input & Click")
        @Description("Verify ElementActions can type, click, and the resulting page state is correct for valid and invalid logins")
        @Severity(SeverityLevel.BLOCKER)
        public void testFormAuthentication(String label, String username, String password,
                        String expectedMessage, String expectedUrlPart) {
                utils.startTest("Form authentication - " + label);

                // Test data table. The password is masked on purpose - never put secrets in reports.
                Allure.parameter("Scenario", label);
                Allure.parameter("Username", username);
                attachText("Test data",
                                "Scenario: " + label + "\n"
                                                + "Username: " + username + "\n"
                                                + "Password: ******\n"
                                                + "Expected flash: " + expectedMessage + "\n"
                                                + "Expected URL part: " + expectedUrlPart);

                Allure.step("Open the login page", () -> {
                        utils.navigateTo(BASE_URL + "/login");
                        attachScreenshot("Login page (empty form)");
                });

                Allure.step("Fill in credentials", () -> {
                        utils.type("username field", page.locator("#username"), username, false);
                        utils.type("password field", page.locator("#password"), password, true);
                        attachScreenshot("Login form filled");
                });

                Locator flash = page.locator("#flash");

                Allure.step("Submit the form and wait for the flash message", () -> {
                        utils.click("Login button", page.locator("button[type='submit']"));
                        utils.waitUntilVisible("flash message", flash);
                        attachScreenshot("Page after login attempt");
                });

                Allure.step("Verify the login result", () -> {
                        // Capture the real text and URL, useful when the assertion below fails
                        attachText("Actual flash message", flash.innerText().trim());
                        attachText("Actual URL", page.url());

                        utils.verify("Flash message", "contains '" + expectedMessage + "'", flash::innerText,
                                        () -> UiAssertionUtils.assertElementContainsText(flash, expectedMessage));

                        utils.verify("URL after login attempt", "contains '" + expectedUrlPart + "'", page::url,
                                        () -> UiAssertionUtils.assertUrlContains(page, expectedUrlPart));
                });
        }

        // ====================================================================
        // Dynamic loading (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "dynamicLoadingExamples")
        @Story("Dynamic Waits")
        @Description("Verify WaitUtils waits for an element that only appears after an async delay")
        @Severity(SeverityLevel.CRITICAL)
        public void testDynamicLoadingRevealsHiddenText(String label, String path) {
                utils.startTest("Dynamic loading - " + label);

                Allure.parameter("Example", label);

                Allure.step("Open the dynamic loading page: " + path, () -> {
                        utils.navigateTo(BASE_URL + path);
                });

                Locator finishText = page.locator("#finish h4");

                Allure.step("Check the text is hidden before clicking Start", () -> {
                        utils.verify("Finish text before clicking Start", "not visible", finishText::isVisible,
                                        () -> Assert.assertFalse(finishText.isVisible(),
                                                        "Finish text should not be visible before Start is clicked"));
                        attachScreenshot("Before clicking Start");
                });

                Allure.step("Click Start and wait for the content to load", () -> {
                        utils.clickStartAndWaitForContent();
                        attachScreenshot("After loading finished");
                });

                Allure.step("Verify the loaded text", () -> {
                        utils.verify("Dynamically loaded text", "Hello World!", finishText::innerText,
                                        () -> UiAssertionUtils.assertElementTextEquals(finishText, "Hello World!"));
                });
        }

        // ====================================================================
        // Checkboxes
        // ====================================================================

        @Test(groups = { "sanity" })
        @Story("Checkbox State")
        @Description("Verify every checkbox flips state when clicked; before/after matrix attached as CSV")
        @Severity(SeverityLevel.NORMAL)
        public void testCheckboxesToggle() {
                utils.startTest("Checkboxes toggle");

                Allure.step("Open the checkboxes page", () -> {
                        utils.navigateTo(BASE_URL + "/checkboxes");
                });

                Locator boxes = page.locator("#checkboxes input[type='checkbox']");

                Allure.step("Verify the number of checkboxes", () -> {
                        utils.verify("Number of checkboxes", 2, boxes::count,
                                        () -> UiAssertionUtils.assertElementCount(boxes, 2));
                });

                // Collect the before/after states inside a step, then reuse them below
                List<boolean[]> rows = Allure.step("Toggle every checkbox", () -> {
                        attachScreenshot("Checkboxes before toggling");
                        List<boolean[]> result = utils.toggleAllCheckboxes(boxes);
                        attachScreenshot("Checkboxes after toggling");
                        return result;
                });

                Allure.step("Attach the before/after state matrix", () -> {
                        utils.attachCsv("Checkbox state matrix", utils.toCsv(rows));
                });

                Allure.step("Verify every checkbox changed state", () -> {
                        utils.verify("Every checkbox changed state", "all checkboxes flipped",
                                        () -> utils.toCsv(rows).replace("\n", " | "),
                                        () -> {
                                                Assert.assertFalse(rows.isEmpty(),
                                                                "NOT EXERCISED: no checkbox was toggled");
                                                for (int i = 0; i < rows.size(); i++) {
                                                        Assert.assertNotEquals(rows.get(i)[1], rows.get(i)[0],
                                                                        "Checkbox " + (i + 1)
                                                                                        + " did not change state");
                                                }
                                        });
                });
        }

        // ====================================================================
        // Dropdown (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "dropdownOptions")
        @Story("Dropdown Selection")
        @Description("Verify ElementActions.selectByValue selects the expected option")
        @Severity(SeverityLevel.NORMAL)
        public void testDropdownSelection(String value, String expectedText) {
                utils.startTest("Dropdown selection - " + expectedText);

                Allure.parameter("Value", value);
                Allure.parameter("Expected text", expectedText);

                Allure.step("Open the dropdown page", () -> {
                        utils.navigateTo(BASE_URL + "/dropdown");
                });

                Locator dropdown = page.locator("#dropdown");
                Locator selected = page.locator("#dropdown option:checked");

                Allure.step("Select option with value '" + value + "'", () -> {
                        utils.selectByValue("dropdown", dropdown, value);
                        attachScreenshot("Dropdown after selection");
                });

                Allure.step("Verify the selected option", () -> {
                        utils.verify("Dropdown value", value, dropdown::inputValue,
                                        () -> Assert.assertEquals(dropdown.inputValue(), value));

                        utils.verify("Selected option text", expectedText, selected::textContent,
                                        () -> UiAssertionUtils.assertElementTextEquals(selected, expectedText));
                });
        }

        // ====================================================================
        // Add / Remove elements
        // ====================================================================

        @Test(groups = { "sanity" })
        @Story("Dynamic DOM")
        @Description("Verify adding and removing elements updates the DOM element count")
        @Severity(SeverityLevel.NORMAL)
        public void testAddAndRemoveElements() {
                utils.startTest("Add and remove elements");

                Allure.step("Open the add/remove elements page", () -> {
                        utils.navigateTo(BASE_URL + "/add_remove_elements/");
                });

                Locator added = page.locator("#elements button.added-manually");

                Allure.step("Add 3 elements and verify the count", () -> {
                        utils.clickTimes("Add Element button", page.locator("button[onclick='addElement()']"), 3);

                        utils.verify("Elements after adding 3", 3, added::count,
                                        () -> UiAssertionUtils.assertElementCount(added, 3));
                        attachScreenshot("After adding 3 elements");
                });

                Allure.step("Delete 1 element and verify the count", () -> {
                        utils.click("first Delete button", added.first());

                        utils.verify("Elements after deleting 1", 2, added::count,
                                        () -> UiAssertionUtils.assertElementCount(added, 2));
                        attachScreenshot("After deleting 1 element");
                });
        }

        // ====================================================================
        // JavaScript dialogs (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "dialogScenarios")
        @Story("Dialog Handling")
        @Description("Verify alert / confirm / prompt dialogs are captured, answered, and reflected in the page result")
        @Severity(SeverityLevel.NORMAL)
        public void testJavaScriptDialogs(String label, String triggerSelector, boolean accept,
                        String promptText, String expectedType, String expectedMessage, String expectedResult) {
                utils.startTest("JS dialog - " + label);

                Allure.parameter("Scenario", label);
                Allure.parameter("Accept dialog", String.valueOf(accept));

                Allure.step("Open the JavaScript alerts page", () -> {
                        utils.navigateTo(BASE_URL + "/javascript_alerts");
                });

                // Returns the captured dialog so the next steps can assert on it
                DialogResult dialog = Allure.step(
                                (accept ? "Accept" : "Dismiss") + " the " + label + " dialog", () -> {
                                        DialogResult result = utils.triggerDialog(label, triggerSelector, accept,
                                                        promptText);
                                        attachText("Captured dialog",
                                                        "Type: " + result.type + "\n"
                                                                        + "Message: " + result.message + "\n"
                                                                        + "Prompt text sent: " + promptText);
                                        return result;
                                });

                Allure.step("Verify the dialog details", () -> {
                        utils.verify("Dialog type", expectedType, () -> dialog.type,
                                        () -> Assert.assertEquals(dialog.type, expectedType));

                        utils.verify("Dialog message", expectedMessage, () -> dialog.message,
                                        () -> Assert.assertEquals(dialog.message, expectedMessage));
                });

                Locator result = page.locator("#result");

                Allure.step("Verify the result shown on the page", () -> {
                        attachScreenshot("Page after dialog handled");
                        utils.verify("Result text on the page", expectedResult, result::innerText,
                                        () -> UiAssertionUtils.assertElementTextEquals(result, expectedResult));
                });
        }

        // ====================================================================
        // HTTP status codes (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "statusCodes")
        @Story("Network Response")
        @Description("Verify the navigation response carries the expected status; response headers attached as JSON")
        @Severity(SeverityLevel.MINOR)
        public void testStatusCodePages(int code) {
                utils.startTest("Status code page - " + code);

                Allure.parameter("Expected status", String.valueOf(code));

                Response response = Allure.step("Open the status code page for " + code, () -> {
                        return utils.navigateTo(BASE_URL + "/status_codes/" + code);
                });

                Allure.step("Verify the HTTP response", () -> {
                        Assert.assertNotNull(response, "Navigation returned no response object");

                        attachText("Response summary",
                                        "URL: " + response.url() + "\n"
                                                        + "Status: " + response.status() + " "
                                                        + response.statusText());
                        utils.attachJson("Response headers", response.headers());

                        utils.verify("HTTP status of the page", code, response::status,
                                        () -> Assert.assertEquals(response.status(), code));
                });

                Locator content = page.locator("#content");

                Allure.step("Verify the page body mentions the status code", () -> {
                        attachScreenshot("Status code page body");
                        utils.verify("Page body mentions the status code", "contains '" + code + "'",
                                        content::innerText,
                                        () -> UiAssertionUtils.assertElementContainsText(content,
                                                        String.valueOf(code)));
                });
        }

        // ====================================================================
        // DEMO: deliberate failure (excluded from the default suite)
        // ====================================================================

        @Test(groups = { "demoFailure" })
        @Story("Failure Reporting Demo")
        @Description("Deliberately fails to showcase how failures render (error details, page HTML, screenshots)")
        @Severity(SeverityLevel.TRIVIAL)
        @Issue("DEMO-123")
        public void testDeliberateFailureForReportDemo() {
                utils.startTest("Deliberate failure (report demo)");

                Allure.step("Open the home page", () -> {
                        utils.navigateTo(BASE_URL);
                });

                Locator heading = page.locator("h1");

                // The step turns red in the report; the nested verify holds the error details
                Allure.step("Verify heading against an intentionally wrong value", () -> {
                        utils.verify("Heading equals an intentionally wrong value", "Wrong heading on purpose",
                                        heading::innerText,
                                        () -> UiAssertionUtils.assertElementTextEquals(heading,
                                                        "Wrong heading on purpose"));
                });
        }
}