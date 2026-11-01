package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shared, static utility for building a per-test "assertion evidence table" —
 * a chronological log of every assertion made during a test, each paired with
 * a pass/fail status, failure message (if any), and a screenshot taken at the
 * moment the assertion ran. Rendered as a single HTML table and attached to
 * the Allure report at the end of the test.
 *
 * Usage (in a PlaywrightBaseTest subclass):
 *
 * @BeforeMethod
 *               public void resetEvidence() {
 *               AllureEvidenceLogger.resetLog();
 *               }
 *
 * @AfterMethod
 *              public void attachEvidence() {
 *              AllureEvidenceLogger.attachSummary();
 *              }
 *
 *              ...
 *
 *              AllureEvidenceLogger.assertStep("Assert: heading is not empty",
 *              page, () -> {
 *              Assert.assertFalse(heading.isEmpty(), "Product heading is
 *              empty.");
 *              });
 *
 *              Thread-safe for parallel TestNG execution: the log is stored in
 *              a
 *              ThreadLocal, same pattern as PlaywrightBaseTest's
 *              browser/context/page
 *              ThreadLocals.
 *
 *              Deliberately kept separate from AllureLogger (utils.common),
 *              which is
 *              typed against io.restassured.response.Response for API tests —
 *              mixing
 *              UI evidence-table logic in there would muddy its purpose.
 */
public class AllureEvidenceLogger {

    private static final Logger logger = LoggerFactory.getLogger(AllureEvidenceLogger.class);
    private static final String TABLE_CELL_CLOSE = "</td>";
    private static final ThreadLocal<List<AssertionRecord>> ASSERTION_LOG = ThreadLocal.withInitial(ArrayList::new);

    private AllureEvidenceLogger() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ========================================================================
    // Record type
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
    // Log lifecycle
    // ========================================================================

    /*
     * Clears the assertion log for the current thread. Call from @BeforeMethod.
     */
    public static void resetLog() {
        ASSERTION_LOG.get().clear();
    }

    /*
     * Returns an unmodifiable view of the current thread's assertion log.
     */
    public static List<AssertionRecord> getLog() {
        return Collections.unmodifiableList(ASSERTION_LOG.get());
    }

    /*
     * Removes the ThreadLocal entry entirely for the current thread.
     * Optional — useful in @AfterClass if threads are reused across test
     * classes and you want to avoid holding references longer than needed.
     */
    public static void clearThreadLocal() {
        ASSERTION_LOG.remove();
    }

    // ========================================================================
    // Assertion recording
    // ========================================================================

    /*
     * Runs an assertion inside an Allure step (so it shows pass/fail in the
     * step tree) AND records it — together with a screenshot taken at that
     * moment — into the per-test assertion log used to build the HTML
     * "Assertion Summary" table.
     *
     * Waits for the page to be fully settled (WaitUtils.waitForPageFullyLoaded)
     * before capturing the screenshot, so evidence isn't a half-loaded page.
     *
     * Re-throws the original AssertionError after recording, so TestNG still
     * reports the test as failed exactly as it would with a bare Assert call.
     */
    public static void assertStep(String name, Page page, Runnable assertion) {
        AssertionRecord assertionRecord = new AssertionRecord(name);
        try {
            Allure.step(name, assertion::run);
            assertionRecord.status = "PASS";
        } catch (AssertionError e) {
            assertionRecord.status = "FAIL";
            assertionRecord.message = e.getMessage();
            throw e;
        } finally {
            assertionRecord.screenshotBase64 = captureScreenshotBase64(page);
            ASSERTION_LOG.get().add(assertionRecord);
        }
    }

    private static String captureScreenshotBase64(Page page) {
        try {
            WaitUtils.waitForPageFullyLoaded(page);
            return ScreenCaptureUtils.takePageScreenshotAsBase64(page);
        } catch (Exception e) {
            logger.warn("Could not capture screenshot for assertion evidence: {}", e.getMessage());
            return null;
        }
    }

    // ========================================================================
    // HTML summary generation + Allure attachment
    // ========================================================================

    /*
     * Builds and attaches the HTML assertion summary table for the current
     * thread's log to the Allure report. No-op if the log is empty.
     * Call from @AfterMethod. Clears the log afterward.
     */
    public static void attachSummary() {
        attachSummary("Assertion Summary");
    }

    /*
     * Same as attachSummary(), with a custom attachment name — useful if a
     * test wants multiple distinct summaries (e.g. one per logical phase).
     */
    public static void attachSummary(String attachmentName) {
        List<AssertionRecord> records = ASSERTION_LOG.get();
        if (!records.isEmpty()) {
            String html = buildSummaryHtml(records);
            Allure.addAttachment(attachmentName, "text/html",
                    new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), ".html");
        }
        records.clear();
    }

    /*
     * Builds the HTML table from a list of assertion records without
     * attaching it — exposed separately in case a caller wants the raw HTML
     * (e.g. to attach under a different type, or combine with other content).
     */
    public static String buildSummaryHtml(List<AssertionRecord> records) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>")
                .append("body{margin:0;font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#222;}")
                .append("table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ddd;padding:8px 10px;text-align:left;vertical-align:top;}")
                .append("th{background:#f5f5f5;position:sticky;top:0;}")
                .append(".pass{color:#1a7f37;font-weight:bold;}")
                .append(".fail{color:#c0392b;font-weight:bold;}")
                .append("img{max-width:240px;border:1px solid #ccc;border-radius:4px;}")
                .append("</style></head><body>")
                .append("<table>")
                .append("<tr><th>#</th><th>Assertion</th><th>Status</th><th>Details</th><th>Screenshot</th></tr>");

        int i = 1;
        for (AssertionRecord r : records) {
            String statusClass = "PASS".equals(r.status) ? "pass" : "fail";
            sb.append("<tr>")
                    .append("<td>").append(i++).append(TABLE_CELL_CLOSE)
                    .append("<td>").append(escapeHtml(r.name)).append(TABLE_CELL_CLOSE)
                    .append("<td class=\"").append(statusClass).append("\">").append(r.status).append(TABLE_CELL_CLOSE)
                    .append("<td>").append(escapeHtml(r.message == null ? "" : r.message)).append(TABLE_CELL_CLOSE)
                    .append("<td>");
            if (r.screenshotBase64 != null) {
                sb.append("<img src=\"data:image/png;base64,").append(r.screenshotBase64).append("\"/>");
            } else {
                sb.append("—");
            }
            sb.append(TABLE_CELL_CLOSE).append("</tr>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}