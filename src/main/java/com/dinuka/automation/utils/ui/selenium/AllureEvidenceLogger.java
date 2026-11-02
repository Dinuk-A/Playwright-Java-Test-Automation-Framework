
package com.dinuka.automation.utils.ui.selenium;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shared, static utility for building a per-test assertion evidence table.
 *
 * Each assertion is recorded with:
 * - Assertion name
 * - PASS / FAIL status
 * - Failure message
 * - Screenshot captured when the assertion runs
 *
 * The final HTML summary is attached to the Allure report.
 *
 * Thread-safe for parallel TestNG execution using ThreadLocal.
 */
public class AllureEvidenceLogger {

    private static final Logger logger = LoggerFactory.getLogger(AllureEvidenceLogger.class);

    private static final String TABLE_CELL_CLOSE = "</td>";

    private static final ThreadLocal<List<AssertionRecord>> ASSERTION_LOG = ThreadLocal.withInitial(ArrayList::new);

    private AllureEvidenceLogger() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ========================================================================
    // Assertion Record
    // ========================================================================

    public static class AssertionRecord {

        private final String name;
        private String status = "PASS";
        private String message = "";
        private String screenshotBase64;

        public AssertionRecord(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getScreenshotBase64() {
            return screenshotBase64;
        }
    }

    // ========================================================================
    // Log Lifecycle
    // ========================================================================

    /**
     * Clears the assertion log for the current thread.
     * Call from @BeforeMethod.
     */
    public static void resetLog() {
        ASSERTION_LOG.get().clear();
    }

    /**
     * Returns an unmodifiable view of the current assertion log.
     */
    public static List<AssertionRecord> getLog() {
        return Collections.unmodifiableList(ASSERTION_LOG.get());
    }

    /**
     * Removes the ThreadLocal entry entirely.
     */
    public static void clearThreadLocal() {
        ASSERTION_LOG.remove();
    }

    // ========================================================================
    // Assertion Recording
    // ========================================================================

    /**
     * Executes an assertion inside an Allure step and records its result
     * along with a screenshot.
     *
     * The original assertion error is rethrown so TestNG can report
     * the test failure correctly.
     */
    public static void assertStep(
            String name,
            WebDriver driver,
            Runnable assertion) {

        AssertionRecord assertionRecord = new AssertionRecord(name);

        try {

            Allure.step(name, assertion::run);

            assertionRecord.status = "PASS";

        } catch (AssertionError e) {

            assertionRecord.status = "FAIL";
            assertionRecord.message = e.getMessage();

            throw e;

        } catch (RuntimeException e) {

            assertionRecord.status = "FAIL";
            assertionRecord.message = e.getMessage();

            throw e;

        } finally {

            assertionRecord.screenshotBase64 = captureScreenshotBase64(driver);

            ASSERTION_LOG.get().add(assertionRecord);
        }
    }

    /**
     * Captures a screenshot as Base64 for HTML embedding.
     *
     * Selenium's standard screenshot API captures the current viewport,
     * not necessarily the entire scrollable page.
     */
    private static String captureScreenshotBase64(WebDriver driver) {

        try {

            if (!(driver instanceof TakesScreenshot)) {
                logger.warn("WebDriver does not support screenshots.");
                return null;
            }

            return ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BASE64);

        } catch (WebDriverException e) {

            logger.warn(
                    "Could not capture screenshot for assertion evidence: {}",
                    e.getMessage());

            return null;
        }
    }

    // ========================================================================
    // HTML Summary Generation + Allure Attachment
    // ========================================================================

    /**
     * Attaches the assertion summary using the default attachment name.
     */
    public static void attachSummary() {
        attachSummary("Assertion Summary");
    }

    /**
     * Attaches the assertion summary using a custom attachment name.
     */
    public static void attachSummary(String attachmentName) {

        List<AssertionRecord> records = ASSERTION_LOG.get();

        if (!records.isEmpty()) {

            String html = buildSummaryHtml(records);

            Allure.addAttachment(
                    attachmentName,
                    "text/html",
                    new ByteArrayInputStream(
                            html.getBytes(StandardCharsets.UTF_8)),
                    ".html");
        }

        records.clear();
    }

    /**
     * Builds the HTML assertion summary without attaching it.
     */
    public static String buildSummaryHtml(List<AssertionRecord> records) {

        StringBuilder sb = new StringBuilder();

        sb.append("<html><head><style>")
                .append("body{margin:0;font-family:Arial,Helvetica,sans-serif;")
                .append("font-size:13px;color:#222;}")
                .append("table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ddd;padding:8px 10px;")
                .append("text-align:left;vertical-align:top;}")
                .append("th{background:#f5f5f5;position:sticky;top:0;}")
                .append(".pass{color:#1a7f37;font-weight:bold;}")
                .append(".fail{color:#c0392b;font-weight:bold;}")
                .append("img{max-width:240px;border:1px solid #ccc;")
                .append("border-radius:4px;}")
                .append("</style></head><body>")
                .append("<table>")
                .append("<tr>")
                .append("<th>#</th>")
                .append("<th>Assertion</th>")
                .append("<th>Status</th>")
                .append("<th>Details</th>")
                .append("<th>Screenshot</th>")
                .append("</tr>");

        int i = 1;

        for (AssertionRecord r : records) {

            String statusClass = "PASS".equals(r.status) ? "pass" : "fail";

            sb.append("<tr>")
                    .append("<td>").append(i++).append(TABLE_CELL_CLOSE)
                    .append("<td>").append(escapeHtml(r.name))
                    .append(TABLE_CELL_CLOSE)
                    .append("<td class=\"").append(statusClass).append("\">")
                    .append(escapeHtml(r.status))
                    .append(TABLE_CELL_CLOSE)
                    .append("<td>").append(escapeHtml(
                            r.message == null ? "" : r.message))
                    .append(TABLE_CELL_CLOSE)
                    .append("<td>");

            if (r.screenshotBase64 != null) {

                sb.append("<img src=\"data:image/png;base64,")
                        .append(r.screenshotBase64)
                        .append("\"/>");

            } else {

                sb.append("—");
            }

            sb.append(TABLE_CELL_CLOSE)
                    .append("</tr>");
        }

        sb.append("</table></body></html>");

        return sb.toString();
    }

    // ========================================================================
    // HTML Escaping
    // ========================================================================

    private static String escapeHtml(String s) {

        if (s == null) {
            return "";
        }

        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
