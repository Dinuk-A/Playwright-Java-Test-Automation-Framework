package com.dinuka.automation.client_a.utils.ui_utils;

import com.dinuka.automation.utils.common.AllureLogger;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Helper logic for PlaywrightUISanityTest. Instance-based (one per test method,
 * created in @BeforeMethod), same pattern as PLP_Utils / PDP_Utils.
 */
public class PlaywrightSanityUtils {

    private static final Logger logger = LoggerFactory.getLogger(PlaywrightSanityUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Page page;
    private final ElementActions actions;

    private final List<String> consoleMessages = new CopyOnWriteArrayList<>();
    private final List<String> pageErrors = new CopyOnWriteArrayList<>();
    private final List<String> failedResponses = new CopyOnWriteArrayList<>();

    public PlaywrightSanityUtils(Page page) {
        this.page = page;
        this.actions = new ElementActions(page);
    }

    // =====================================================================
    // Evidence primitives
    // =====================================================================

    /** Ordered key/value map: details("Expected", "x", "Actual", y). */
    public static Map<String, Object> details(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < values.length - 1; i += 2) {
            result.put(String.valueOf(values[i]), values[i + 1]);
        }
        return result;
    }

    public void labelTest(String name) {
        Allure.getLifecycle().updateTestCase(testResult -> testResult.setName(name));
    }

    public void attachDetails(String name, Map<String, Object> details) {
        AllureLogger.logCustomFields(name, details);
    }

    public void attachText(String name, String content) {
        AllureLogger.logText(name, content);
    }

    public void attachCsv(String name, String csv) {
        Allure.addAttachment(name, "text/csv", csv, ".csv");
    }

    public void attachJson(String name, Map<String, ?> data) {
        try {
            Allure.addAttachment(name, "application/json",
                    MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data), ".json");
        } catch (Exception e) {
            attachText(name + " (serialization failed)", String.valueOf(data));
        }
    }

    public void screenshot(String name) {
        byte[] bytes = ScreenCaptureUtils.takePageScreenshotAsBytes(page);
        if (bytes != null && bytes.length > 0) {
            AllureLogger.logScreenshot(name, bytes);
        }
    }

    /** Values are read lazily so "Actual" reflects the moment the step ran. */
    private Map<String, Object> safe(Supplier<Map<String, Object>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return details("Evidence collection error", e.getMessage());
        }
    }

    /** Action step: details + screenshot attached inside the step, even on failure. */
    public void step(String name, Supplier<Map<String, Object>> stepDetails, Runnable action) {
        Allure.step(name, () -> {
            try {
                action.run();
            } finally {
                attachDetails(name + " - Step Details", safe(stepDetails));
                screenshot(name + " - Screenshot");
            }
        });
    }

    /** Same as step(...) but returns the action's result. */
    public <T> T stepReturning(String name, Supplier<Map<String, Object>> stepDetails, Supplier<T> action) {
        AtomicReference<T> result = new AtomicReference<>();
        step(name, stepDetails, () -> result.set(action.get()));
        return result.get();
    }

    /** Assertion step: AllureEvidenceLogger row + assertion values + screenshot in the step. */
    public void verify(String name, Supplier<Map<String, Object>> assertionDetails, Runnable assertion) {
        Allure.step("Verify: " + name, () -> {
            try {
                AllureEvidenceLogger.assertStep("Assert: " + name, page, assertion);
            } finally {
                attachDetails(name + " - Assertion Values", safe(assertionDetails));
                screenshot(name + " - Screenshot");
            }
        });
    }

    // =====================================================================
    // Diagnostics (console / page errors / failed responses)
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

    private void attachDiagnostics() {
        attachText("Browser Console Log", consoleMessages.isEmpty() ? "(none)" : String.join("\n", consoleMessages));
        attachText("Page Errors (uncaught JS)", pageErrors.isEmpty() ? "(none)" : String.join("\n", pageErrors));
        attachText("HTTP Responses >= 400 (informational)",
                failedResponses.isEmpty() ? "(none)" : String.join("\n", failedResponses));
    }

    /** Call from @AfterMethod. Always clears the AllureEvidenceLogger ThreadLocal. */
    public void attachFinalEvidence(ITestResult result) {
        try {
            if (page != null) {
                if (result.getStatus() == ITestResult.FAILURE) {
                    AllureLogger.logError("Test Failure", result.getThrowable());
                    Allure.addAttachment("Page HTML at failure", "text/html", page.content(), ".html");
                }
                attachDetails("Final Page State", details("URL", page.url(), "Title", page.title()));
                screenshot("Final Test State");
                attachDiagnostics();
            }
        } catch (Exception e) {
            logger.warn("Evidence collection warning: {}", e.getMessage());
            attachText("Evidence Collection Warning", String.valueOf(e.getMessage()));
        } finally {
            try {
                AllureEvidenceLogger.attachSummary();
            } finally {
                AllureEvidenceLogger.clearThreadLocal();
            }
        }
    }

    // =====================================================================
    // Navigation
    // =====================================================================

    public Response navigate(String url) {
        Response response = page.navigate(url);
        WaitUtils.waitForPageLoad(page);
        return response;
    }

    // =====================================================================
    // Login
    // =====================================================================

    public void enterCredentials(String username, String password) {
        actions.type(page.locator("#username"), username);
        actions.type(page.locator("#password"), password);
    }

    public void submitLogin() {
        actions.click(page.locator("button[type='submit']"));
        WaitUtils.waitForElementVisible(page.locator("#flash"));
    }

    // =====================================================================
    // Dynamic loading
    // =====================================================================

    /** Clicks Start and waits for the finish text. Returns elapsed millis. */
    public long startDynamicLoadAndWait() {
        long start = System.currentTimeMillis();
        actions.click(page.locator("#start button"));
        WaitUtils.waitForElementVisible(page.locator("#finish h4"), 15000);
        return System.currentTimeMillis() - start;
    }

    // =====================================================================
    // Checkboxes
    // =====================================================================

    /** Toggles every checkbox; returns rows of {index, before, after}. */
    public List<boolean[]> toggleAllCheckboxes(Locator boxes) {
        List<boolean[]> rows = new ArrayList<>();
        int count = boxes.count();
        for (int i = 0; i < count; i++) {
            boolean before = boxes.nth(i).isChecked();
            actions.click(boxes.nth(i));
            rows.add(new boolean[] { before, boxes.nth(i).isChecked() });
        }
        return rows;
    }

    public String toCsv(List<boolean[]> rows) {
        StringBuilder sb = new StringBuilder("index,before,after\n");
        for (int i = 0; i < rows.size(); i++) {
            sb.append(i).append(',').append(rows.get(i)[0]).append(',').append(rows.get(i)[1]).append('\n');
        }
        return sb.toString();
    }

    // =====================================================================
    // Dropdown / Add-Remove
    // =====================================================================

    public void selectDropdownValue(String value) {
        actions.selectByValue(page.locator("#dropdown"), value);
    }

    public void clickRepeatedly(Locator locator, int times) {
        for (int i = 0; i < times; i++) {
            actions.click(locator);
        }
    }

    public void clickFirst(Locator locator) {
        actions.click(locator.first());
    }

    // =====================================================================
    // JS dialogs
    // =====================================================================

    /** Registers a one-shot dialog handler, clicks the trigger, returns {type, message}. */
    public Map<String, Object> triggerDialog(String triggerSelector, boolean accept, String promptText) {
        AtomicReference<String> type = new AtomicReference<>("(no dialog fired)");
        AtomicReference<String> message = new AtomicReference<>("(no dialog fired)");

        page.onceDialog(dialog -> {
            type.set(dialog.type());
            message.set(dialog.message());
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
        return details("type", type.get(), "message", message.get());
    }
}