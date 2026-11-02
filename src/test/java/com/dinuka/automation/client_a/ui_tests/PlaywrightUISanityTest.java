package com.dinuka.automation.client_a.ui_tests;

import static com.dinuka.automation.client_a.utils.ui_utils.PlaywrightSanityUtils.details;

import com.dinuka.automation.client_a.utils.ui_utils.PlaywrightSanityUtils;
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
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

@Epic("Framework Sanity Checks")
@Feature("Playwright UI Sanity")
@Owner("Dinuka")
public class PlaywrightUISanityTest extends PlaywrightBaseTest {

        private static final String BASE_URL = ConfigPropertyReader.getProperty("sanity.ui.base.url");

        private PlaywrightSanityUtils utils;

        // ====================================================================
        // Lifecycle
        // ====================================================================

        @BeforeMethod(alwaysRun = true)
        public void setUpSanity() {
                AllureEvidenceLogger.resetLog();
                utils = new PlaywrightSanityUtils(page);
                utils.startDiagnostics();
                Allure.label("layer", "ui");
                Allure.link("Application under test", BASE_URL);
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

        // { label, triggerSelector, accept, promptText, expectedType, expectedMessage,
        // expectedResult }
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
        // TC-SAN-001: Home page
        // ====================================================================

        @Test(groups = { "sanity" })
        @Story("Navigation & Assertions")
        @Description("Verify Playwright can launch a browser, navigate, and read page content")
        @Severity(SeverityLevel.BLOCKER)
        @TmsLink("TC-SAN-001")
        public void testHomePageLoadsAndLinksArePresent() {
                utils.step("Navigate to Home Page", () -> details("Target URL", BASE_URL),
                                () -> utils.navigate(BASE_URL));

                Locator heading = page.locator("h1");
                Locator links = page.locator("#content ul li a");

                utils.verify("Page title is 'The Internet'",
                                () -> details("Expected", "The Internet", "Actual", page.title()),
                                () -> UiAssertionUtils.assertPageTitle(page, "The Internet"));

                utils.verify("Heading is visible",
                                () -> details("Expected visible", true, "Actual visible", heading.isVisible()),
                                () -> UiAssertionUtils.assertElementVisible(heading));

                utils.verify("Heading text",
                                () -> details("Expected contains", "Welcome to the-internet",
                                                "Actual", heading.innerText()),
                                () -> UiAssertionUtils.assertElementContainsText(heading,
                                                "Welcome to the-internet"));

                // Vacuous-truth guard: the loop below must have items to iterate.
                utils.verify("Example link list is not empty (guard)",
                                () -> details("Expected count", "> 0", "Actual count", links.count()),
                                () -> Assert.assertTrue(links.count() > 0,
                                                "NOT EXERCISED: example link list is empty"));

                List<String> hrefs = links.evaluateAll("els => els.map(e => e.getAttribute('href'))") instanceof List<?>
                                ? castList(links.evaluateAll("els => els.map(e => e.getAttribute('href'))"))
                                : List.of();
                utils.attachText("Example link hrefs", String.join("\n", hrefs));

                utils.verify("Checkboxes link points to /checkboxes",
                                () -> details("Expected href", "/checkboxes"),
                                () -> UiAssertionUtils.assertAttributeEquals(
                                                page.locator("#content a[href='/checkboxes']"), "href",
                                                "/checkboxes"));
        }

        @SuppressWarnings("unchecked")
        private static List<String> castList(Object value) {
                return (List<String>) value;
        }

        // ====================================================================
        // TC-SAN-002: Form authentication (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "loginScenarios")
        @Story("Form Input & Click")
        @Description("Verify ElementActions can type, click, and the resulting page state is correct for valid and invalid logins")
        @Severity(SeverityLevel.BLOCKER)
        @TmsLink("TC-SAN-002")
        public void testFormAuthentication(String label, String username, String password,
                        String expectedMessage, String expectedUrlPart) {
                utils.labelTest("Form authentication - " + label);
                Allure.parameter("scenario", label);
                Allure.parameter("username", username);
                Allure.parameter("password", "[REDACTED]");

                utils.step("Navigate to Login Page", () -> details("Target URL", BASE_URL + "/login"),
                                () -> utils.navigate(BASE_URL + "/login"));

                utils.step("Enter credentials",
                                () -> details("Username", username, "Password", "[REDACTED]",
                                                "Input method", "ElementActions.type()"),
                                () -> utils.enterCredentials(username, password));

                utils.step("Submit login form",
                                () -> details("Action", "Click submit", "Wait for", "#flash visible"),
                                () -> utils.submitLogin());

                Locator flash = page.locator("#flash");

                utils.verify("Flash message: " + expectedMessage,
                                () -> details("Expected contains", expectedMessage, "Actual", flash.innerText()),
                                () -> UiAssertionUtils.assertElementContainsText(flash, expectedMessage));

                utils.verify("URL contains " + expectedUrlPart,
                                () -> details("Expected contains", expectedUrlPart, "Actual", page.url()),
                                () -> UiAssertionUtils.assertUrlContains(page, expectedUrlPart));
        }

        // ====================================================================
        // TC-SAN-003: Dynamic loading (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "dynamicLoadingExamples")
        @Story("Dynamic Waits")
        @Description("Verify WaitUtils waits for an element that only appears after an async delay")
        @Severity(SeverityLevel.CRITICAL)
        @TmsLink("TC-SAN-003")
        public void testDynamicLoadingRevealsHiddenText(String label, String path) {
                utils.labelTest("Dynamic loading - " + label);
                Allure.parameter("example", label);

                utils.step("Navigate to " + path, () -> details("Target URL", BASE_URL + path),
                                () -> utils.navigate(BASE_URL + path));

                Locator finishText = page.locator("#finish h4");

                utils.verify("Finish text is not visible before Start",
                                () -> details("Expected visible", false, "Actual visible", finishText.isVisible()),
                                () -> Assert.assertFalse(finishText.isVisible(),
                                                "Finish text should not be visible before Start is clicked"));

                long elapsed = utils.stepReturning("Click Start and wait for async content",
                                () -> details("Action", "Click Start", "Wait", "#finish h4 visible (15s max)"),
                                utils::startDynamicLoadAndWait);
                Allure.parameter("dynamic load duration (ms)", elapsed);

                utils.verify("Dynamically loaded text",
                                () -> details("Expected", "Hello World!", "Actual", finishText.innerText(),
                                                "Load duration (ms)", elapsed),
                                () -> UiAssertionUtils.assertElementTextEquals(finishText, "Hello World!"));
        }

        // ====================================================================
        // TC-SAN-004: Checkboxes
        // ====================================================================

        @Test(groups = { "sanity" })
        @Story("Checkbox State")
        @Description("Verify every checkbox flips state when clicked; before/after matrix attached as CSV")
        @Severity(SeverityLevel.NORMAL)
        @TmsLink("TC-SAN-004")
        public void testCheckboxesToggle() {
                utils.step("Navigate to Checkboxes page", () -> details("Target URL", BASE_URL + "/checkboxes"),
                                () -> utils.navigate(BASE_URL + "/checkboxes"));

                Locator boxes = page.locator("#checkboxes input[type='checkbox']");

                utils.verify("Checkboxes are present (guard)",
                                () -> details("Expected count", "> 0", "Actual count", boxes.count()),
                                () -> UiAssertionUtils.assertElementCount(boxes, 2));

                List<boolean[]> rows = utils.stepReturning("Toggle every checkbox",
                                () -> details("Checkbox count", boxes.count()),
                                () -> utils.toggleAllCheckboxes(boxes));

                utils.attachCsv("Checkbox state matrix", utils.toCsv(rows));

                utils.verify("Every checkbox flipped state",
                                () -> details("Rows (before->after)", utils.toCsv(rows)),
                                () -> {
                                        Assert.assertFalse(rows.isEmpty(), "NOT EXERCISED: no checkbox was toggled");
                                        for (int i = 0; i < rows.size(); i++) {
                                                Assert.assertNotEquals(rows.get(i)[1], rows.get(i)[0],
                                                                "Checkbox " + i + " did not change state");
                                        }
                                });
        }

        // ====================================================================
        // TC-SAN-005: Dropdown (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "dropdownOptions")
        @Story("Dropdown Selection")
        @Description("Verify ElementActions.selectByValue selects the expected option")
        @Severity(SeverityLevel.NORMAL)
        @TmsLink("TC-SAN-005")
        public void testDropdownSelection(String value, String expectedText) {
                utils.labelTest("Dropdown selection - value " + value);
                Allure.parameter("option value", value);

                utils.step("Navigate to Dropdown page", () -> details("Target URL", BASE_URL + "/dropdown"),
                                () -> utils.navigate(BASE_URL + "/dropdown"));

                utils.step("Select option by value", () -> details("Value", value),
                                () -> utils.selectDropdownValue(value));

                Locator dropdown = page.locator("#dropdown");

                utils.verify("Dropdown value is " + value,
                                () -> details("Expected", value, "Actual", dropdown.inputValue()),
                                () -> Assert.assertEquals(dropdown.inputValue(), value));

                utils.verify("Selected option text is '" + expectedText + "'",
                                () -> details("Expected", expectedText, "Actual",
                                                page.locator("#dropdown option:checked").textContent().trim()),
                                () -> UiAssertionUtils.assertElementTextEquals(
                                                page.locator("#dropdown option:checked"), expectedText));
        }

        // ====================================================================
        // TC-SAN-006: Add / Remove elements
        // ====================================================================

        @Test(groups = { "sanity" })
        @Story("Dynamic DOM")
        @Description("Verify adding and removing elements updates the DOM element count")
        @Severity(SeverityLevel.NORMAL)
        @TmsLink("TC-SAN-006")
        public void testAddAndRemoveElements() {
                utils.step("Navigate to Add/Remove Elements page",
                                () -> details("Target URL", BASE_URL + "/add_remove_elements/"),
                                () -> utils.navigate(BASE_URL + "/add_remove_elements/"));

                Locator added = page.locator("#elements button.added-manually");

                utils.step("Add 3 elements", () -> details("Clicks", 3),
                                () -> utils.clickRepeatedly(page.locator("button[onclick='addElement()']"), 3));

                utils.verify("3 elements added",
                                () -> details("Expected", 3, "Actual", added.count()),
                                () -> UiAssertionUtils.assertElementCount(added, 3));

                utils.step("Remove one element", () -> details("Action", "Click first Delete"),
                                () -> utils.clickFirst(added));

                utils.verify("2 elements remain",
                                () -> details("Expected", 2, "Actual", added.count()),
                                () -> UiAssertionUtils.assertElementCount(added, 2));
        }

        // ====================================================================
        // TC-SAN-007: JavaScript dialogs (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "dialogScenarios")
        @Story("Dialog Handling")
        @Description("Verify alert / confirm / prompt dialogs are captured, answered, and reflected in the page result")
        @Severity(SeverityLevel.NORMAL)
        @TmsLink("TC-SAN-007")
        public void testJavaScriptDialogs(String label, String triggerSelector, boolean accept,
                        String promptText, String expectedType, String expectedMessage, String expectedResult) {
                utils.labelTest("JS dialog - " + label);
                Allure.parameter("dialog", label);

                utils.step("Navigate to JavaScript Alerts page",
                                () -> details("Target URL", BASE_URL + "/javascript_alerts"),
                                () -> utils.navigate(BASE_URL + "/javascript_alerts"));

                java.util.Map<String, Object> dialog = utils.stepReturning("Trigger and answer dialog",
                                () -> details("Trigger", triggerSelector, "Accept", accept,
                                                "Prompt text", promptText),
                                () -> utils.triggerDialog(triggerSelector, accept, promptText));

                utils.verify("Dialog type is " + expectedType,
                                () -> details("Expected", expectedType, "Actual", dialog.get("type")),
                                () -> Assert.assertEquals(dialog.get("type"), expectedType));

                utils.verify("Dialog message",
                                () -> details("Expected", expectedMessage, "Actual", dialog.get("message")),
                                () -> Assert.assertEquals(dialog.get("message"), expectedMessage));

                Locator result = page.locator("#result");
                utils.verify("Page result text",
                                () -> details("Expected", expectedResult, "Actual", result.innerText()),
                                () -> UiAssertionUtils.assertElementTextEquals(result, expectedResult));
        }

        // ====================================================================
        // TC-SAN-008: HTTP status codes (data driven)
        // ====================================================================

        @Test(groups = { "sanity" }, dataProvider = "statusCodes")
        @Story("Network Response")
        @Description("Verify the navigation response carries the expected status; response headers attached as JSON")
        @Severity(SeverityLevel.MINOR)
        @TmsLink("TC-SAN-008")
        public void testStatusCodePages(int code) {
                utils.labelTest("Status code page - " + code);
                Allure.parameter("status code", code);

                Response response = utils.stepReturning("Navigate to /status_codes/" + code,
                                () -> details("Target URL", BASE_URL + "/status_codes/" + code),
                                () -> utils.navigate(BASE_URL + "/status_codes/" + code));

                Assert.assertNotNull(response, "Navigation returned no response object");
                utils.attachJson("Response headers (" + code + ")", response.headers());

                utils.verify("Response status is " + code,
                                () -> details("Expected", code, "Actual", response.status(),
                                                "URL", response.url()),
                                () -> Assert.assertEquals(response.status(), code));

                utils.verify("Page body mentions the status code",
                                () -> details("Expected contains", String.valueOf(code)),
                                () -> UiAssertionUtils.assertElementContainsText(page.locator("#content"),
                                                String.valueOf(code)));
        }

        // ====================================================================
        // DEMO: deliberate failure - shows failed step, error details, page
        // HTML and failure screenshot in Allure. Excluded from the default suite.
        // ====================================================================

        @Test(groups = { "demoFailure" })
        @Story("Failure Reporting Demo")
        @Description("Deliberately fails to showcase how failures render (error attachment, page HTML, screenshot)")
        @Severity(SeverityLevel.TRIVIAL)
        @Issue("DEMO-123")
        public void testDeliberateFailureForReportDemo() {
                utils.step("Navigate to Home Page", () -> details("Target URL", BASE_URL),
                                () -> utils.navigate(BASE_URL));

                Locator heading = page.locator("h1");
                utils.verify("Heading equals an intentionally wrong value",
                                () -> details("Expected", "Wrong heading on purpose",
                                                "Actual", heading.innerText()),
                                () -> UiAssertionUtils.assertElementTextEquals(heading,
                                                "Wrong heading on purpose"));
        }
}