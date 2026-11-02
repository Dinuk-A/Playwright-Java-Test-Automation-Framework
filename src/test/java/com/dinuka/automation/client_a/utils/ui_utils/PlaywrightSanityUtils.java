package com.dinuka.automation.client_a.utils.ui_utils;

import com.dinuka.automation.utils.ui.playwright.AllureEvidenceLogger;
import com.dinuka.automation.utils.ui.playwright.ElementActions;
import com.dinuka.automation.utils.ui.playwright.ScreenCaptureUtils;
import com.dinuka.automation.utils.ui.playwright.WaitUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Helper logic for PlaywrightUISanityTest. One instance per test method
 * (created in @BeforeMethod), same pattern as PLP_Utils / PDP_Utils.
 */
public class PlaywrightSanityUtils {

    private static final Logger logger = LoggerFactory.getLogger(PlaywrightSanityUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Page page;
    private final ElementActions actions;

    private final List<String> consoleMessages = new CopyOnWriteArrayList<>();
    private final List<String> pageErrors = new CopyOnWriteArrayList<>();
    private final List<String> failedResponses = new CopyOnWriteArrayList<>();

    public static class DialogResult {
        public final String type;
        public final String message;

        DialogResult(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }

    public PlaywrightSanityUtils(Page page) {
        this.page = page;
        this.actions = new ElementActions(page);
    }

    // =====================================================================
    // Test header / attachments
    // =====================================================================

    /** Sets the report name and removes the auto-added parameters (arg0.., browser, headless). */
    public void startTest(String testName) {
        Allure.getLifecycle().updateTestCase(tr -> {
            tr.setName(testName);
            tr.getParameters().clear();
        });
    }

    public void info(String text) {
        Allure.step(text);
    }

    public void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    public void attachCsv(String name, String csv) {
        Allure.addAttachment(name, "text/csv", csv, ".csv");
    }

    public void attachJson(String name, Map<String, ?> data) {
        try {
            Allure.addAttachment(name, "application/json",
                    MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data), ".json");
        } catch (Exception e) {
            attachText(name, String.valueOf(data));
        }
    }

    public void screenshot(String name) {
        byte[] bytes = ScreenCaptureUtils.takePageScreenshotAsBytes(page);
        if (bytes != null && bytes.length > 0) {
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), "png");
        }
    }

    // =====================================================================
    // Verification
    // =====================================================================

    /**
     * Verify step containing: what is checked, expected, actual, a screenshot
     * and the assertion result. The text is written before the assertion runs,
     * so it is present in the report even when the assertion fails.
     */
    public void verify(String what, Object expected, Supplier<Object> actual, Runnable assertion) {
        Allure.step("Verify: " + what, () -> {
            Allure.step("Checking: " + what);
            Allure.step("Expected: " + expected);
            Allure.step("Actual: " + read(actual));
            screenshot(what + " - Screenshot");
            AllureEvidenceLogger.assertStep("Assert: " + what, page, assertion);
        });
    }

    private String read(Supplier<Object> supplier) {
        try {
            return String.valueOf(supplier.get());
        } catch (Exception e) {
            return "(could not read value: " + e.getMessage() + ")";
        }
    }

    // =====================================================================
    // Actions (each is one step with text + screenshots)
    // =====================================================================

    public Response navigateTo(String url) {
        AtomicReference<Response> response = new AtomicReference<>();
        Allure.step("Navigating to " + url, () -> {
            response.set(page.navigate(url));
            WaitUtils.waitForPageLoad(page);
            screenshot("Page loaded - " + url);
        });
        return response.get();
    }

    public void click(String description, Locator locator) {
        Allure.step("Clicking " + description, () -> {
            screenshot("Before clicking " + description);
            actions.click(locator);
            page.waitForTimeout(300);
            screenshot("After clicking " + description);
        });
    }

    public void clickTimes(String description, Locator locator, int times) {
        for (int i = 1; i <= times; i++) {
            click(description + " (" + i + " of " + times + ")", locator);
        }
    }

    public void type(String description, Locator locator, String text, boolean masked) {
        Allure.step("Entering " + (masked ? "[hidden]" : "'" + text + "'") + " in " + description, () -> {
            actions.type(locator, text);
            screenshot("After entering " + description);
        });
    }

    public void selectByValue(String description, Locator locator, String value) {
        Allure.step("Selecting value '" + value + "' in " + description, () -> {
            screenshot("Before selecting in " + description);
            actions.selectByValue(locator, value);
            screenshot("After selecting in " + description);
        });
    }

    public void waitUntilVisible(String description, Locator locator) {
        Allure.step("Waiting for " + description + " to be visible", () -> {
            WaitUtils.waitForElementVisible(locator);
            screenshot(description + " visible");
        });
    }

    /** Clicks Start, waits for the finish text, returns the elapsed millis. */
    public long clickStartAndWaitForContent() {
        click("Start button", page.locator("#start button"));
        long start = System.currentTimeMillis();
        Allure.step("Waiting for the dynamically loaded text (up to 15s)", () -> {
            WaitUtils.waitForElementVisible(page.locator("#finish h4"), 15000);
            screenshot("Dynamic content loaded");
        });
        long elapsed = System.currentTimeMillis() - start;
        info("Content appeared after " + elapsed + " ms");
        return elapsed;
    }

    /** Clicks every checkbox; returns rows of {before, after}. */
    public List<boolean[]> toggleAllCheckboxes(Locator boxes) {
        List<boolean[]> rows = new ArrayList<>();
        int count = boxes.count();
        for (int i = 0; i < count; i++) {
            boolean before = boxes.nth(i).isChecked();
            click("checkbox " + (i + 1) + " (currently " + (before ? "checked" : "unchecked") + ")", boxes.nth(i));
            rows.add(new boolean[] { before, boxes.nth(i).isChecked() });
        }
        return rows;
    }

    public String toCsv(List<boolean[]> rows) {
        StringBuilder sb = new StringBuilder("checkbox,before,after\n");
        for (int i = 0; i < rows.size(); i++) {
            sb.append(i + 1).append(',').append(rows.get(i)[0]).append(',').append(rows.get(i)[1]).append('\n');
        }
        return sb.toString();
    }

    /** Registers a one-shot dialog handler, clicks the trigger, waits for the page result. */
    public DialogResult triggerDialog(String description, String triggerSelector, boolean accept,
            String promptText) {
        AtomicReference<DialogResult> result = new AtomicReference<>(new DialogResult("(none)", "(none)"));

        Allure.step("Triggering JS dialog: " + description, () -> {
            screenshot("Before triggering " + description);

            page.onceDialog(dialog -> {
                result.set(new DialogResult(dialog.type(), dialog.message()));
                if (!accept) {
                    dialog.dismiss();
                } else if (promptText != null) {
                    dialog.accept(promptText);
                } else {
                    dialog.accept();
                }
            });

            actions.click(page.locator(triggerSelector));
            WaitUtils.waitForElementVisible(page.locator("#result"));

            info("Dialog appeared: type='" + result.get().type + "', message='" + result.get().message + "'");
            info("Dialog was " + (accept ? "accepted" + (promptText != null ? " with text '" + promptText + "'" : "")
                    : "dismissed"));
            screenshot("After answering " + description);
        });
        return result.get();
    }

    // =====================================================================
    // Diagnostics + final evidence
    // =====================================================================

    public void startDiagnostics() {
        page.onConsoleMessage(m -> consoleMessages.add("[" + m.type() + "] " + m.text()));
        page.onPageError(e -> pageErrors.add(e));
        page.onResponse(r -> {
            if (r.status() >= 400) {
                failedResponses.add(r.status() + " " + r.request().method() + " " + r.url());
            }
        });
    }

    /** Call from @AfterMethod. Always clears the AllureEvidenceLogger ThreadLocal. */
    public void attachFinalEvidence(ITestResult result) {
        try {
            if (page != null) {
                if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null) {
                    StringWriter sw = new StringWriter();
                    result.getThrowable().printStackTrace(new PrintWriter(sw));
                    attachText("Failure details", sw.toString());
                    Allure.addAttachment("Page HTML at failure", "text/html", page.content(), ".html");
                }
                attachText("Final page state", "URL: " + page.url() + "\nTitle: " + page.title());
                screenshot("Final test state");
                attachText("Browser console log", joinOrNone(consoleMessages));
                attachText("Uncaught page errors", joinOrNone(pageErrors));
                attachText("HTTP responses >= 400 (informational)", joinOrNone(failedResponses));
            }
        } catch (Exception e) {
            logger.warn("Evidence collection warning: {}", e.getMessage());
        } finally {
            try {
                AllureEvidenceLogger.attachSummary();
            } finally {
                AllureEvidenceLogger.clearThreadLocal();
            }
        }
    }

    private String joinOrNone(List<String> lines) {
        return lines.isEmpty() ? "(none)" : String.join("\n", lines);
    }
}