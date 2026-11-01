
package com.dinuka.automation.utils.api.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Validates a JSON response against a JSON Schema file (draft 2020-12).
 *
 * - Every violation is collected and attached to Allure as one HTML table,
 * then a single assertion fails with the full list.
 * - Unknown fields (not declared in the schema) are "drift": warnings by
 * default, failures when -Dapi.schema.strict=true.
 */
public final class SchemaValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(SchemaValidationUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String STRICT_SYSTEM_PROPERTY = "api.schema.strict";
    private static final String DRIFT_KEYWORD = "additionalProperties";

    // Compiled once per schema path; safe for parallel TestNG execution.
    private static final Map<String, JsonSchema> SCHEMA_CACHE = new ConcurrentHashMap<>();

    private SchemaValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isStrictMode() {
        return Boolean.parseBoolean(System.getProperty(STRICT_SYSTEM_PROPERTY, "false"));
    }

    /**
     * Validates {@code actual} against the schema file and fails with the full
     * violation list. Drift (unknown fields) fails only in strict mode.
     */
    public static void assertMatchesSchema(JsonNode actual, String schemaPath, String checkTitle) {
        Assert.assertNotNull(actual, "Response JSON is null - nothing to validate against " + schemaPath);

        boolean strict = isStrictMode();
        Set<ValidationMessage> messages = loadSchema(schemaPath).validate(actual);

        List<ValidationMessage> violations = new ArrayList<>();
        List<ValidationMessage> drift = new ArrayList<>();
        for (ValidationMessage message : messages) {
            if (DRIFT_KEYWORD.equals(message.getType())) {
                drift.add(message);
            } else {
                violations.add(message);
            }
        }

        attachEvidence(checkTitle, schemaPath, strict, violations, drift);

        if (!drift.isEmpty()) {
            logger.warn("{}: {} unknown field(s) not in schema (strict={}): {}",
                    checkTitle, drift.size(), strict, describe(drift));
        }

        List<ValidationMessage> failing = new ArrayList<>(violations);
        if (strict) {
            failing.addAll(drift);
        }
        Assert.assertTrue(failing.isEmpty(),
                checkTitle + " failed with " + failing.size() + " violation(s):\n" + describe(failing));
    }

    // ----------------------------------------------------------------------
    // Schema loading
    // ----------------------------------------------------------------------

    private static JsonSchema loadSchema(String schemaPath) {
        return SCHEMA_CACHE.computeIfAbsent(schemaPath, path -> {
            Path file = Paths.get(path);
            if (!Files.exists(file)) {
                throw new IllegalStateException("Schema file not found: " + file.toAbsolutePath());
            }
            try {
                JsonNode tree = MAPPER.readTree(file.toFile());
                if (tree == null || !tree.isObject()) {
                    throw new IllegalStateException("Schema file is empty or not a JSON object: " + path);
                }
                // Unknown-field detection: every declared object gets
                // additionalProperties:false in an in-memory copy only.
                // The file on disk stays lenient and human-friendly.
                JsonNode strictCopy = tree.deepCopy();
                forbidUndeclaredProperties(strictCopy);
                return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(strictCopy);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read schema file: " + path, e);
            }
        });
    }

    /**
     * Adds "additionalProperties": false to every schema node that declares
     * "properties" for an object type and does not already define it.
     * Nodes without "type":"object" (e.g. the if/then branches) are left alone.
     */
    private static void forbidUndeclaredProperties(JsonNode node) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            if (object.has("properties") && !object.has("additionalProperties") && declaresObjectType(object)) {
                object.put("additionalProperties", false);
            }
            Iterator<JsonNode> children = object.elements();
            while (children.hasNext()) {
                forbidUndeclaredProperties(children.next());
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                forbidUndeclaredProperties(child);
            }
        }
    }

    private static boolean declaresObjectType(ObjectNode schemaNode) {
        JsonNode type = schemaNode.get("type");
        if (type == null) {
            return false;
        }
        if (type.isTextual()) {
            return "object".equals(type.asText());
        }
        if (type.isArray()) {
            for (JsonNode t : (ArrayNode) type) {
                if ("object".equals(t.asText())) {
                    return true;
                }
            }
        }
        return false;
    }

    // ----------------------------------------------------------------------
    // Reporting
    // ----------------------------------------------------------------------

    private static String describe(List<ValidationMessage> messages) {
        StringBuilder sb = new StringBuilder();
        for (ValidationMessage message : messages) {
            sb.append(" - ").append(message.getMessage()).append("\n");
        }
        return sb.toString();
    }

    private static void attachEvidence(String checkTitle, String schemaPath, boolean strict,
            List<ValidationMessage> violations, List<ValidationMessage> drift) {
        boolean failed = !violations.isEmpty() || (strict && !drift.isEmpty());

        StringBuilder rows = new StringBuilder();
        for (ValidationMessage message : violations) {
            rows.append(row("VIOLATION", "fail", message));
        }
        for (ValidationMessage message : drift) {
            rows.append(row(strict ? "UNKNOWN FIELD (strict)" : "UNKNOWN FIELD (warning)",
                    strict ? "fail" : "warn", message));
        }
        if (rows.length() == 0) {
            rows.append("<tr><td colspan='3'><i>No violations and no unknown fields.</i></td></tr>");
        }

        String html = "<!DOCTYPE html><html><head><style>"
                + "body{font-family:Arial,Helvetica,sans-serif;margin:10px;font-size:13px;color:#222}"
                + "table{width:100%;border-collapse:collapse;margin-bottom:16px}"
                + "th,td{padding:8px 10px;border:1px solid #ddd;text-align:left;vertical-align:top}"
                + "th{background:#f4f4f4}"
                + ".pass{color:#28a745;font-weight:bold}"
                + ".fail{color:#dc3545;font-weight:bold}"
                + ".warn{color:#b8860b;font-weight:bold}"
                + "</style></head><body>"
                + "<h3>" + escapeHtml(checkTitle) + "</h3>"
                + "<table>"
                + "<tr><th>Schema file</th><td>" + escapeHtml(schemaPath) + "</td></tr>"
                + "<tr><th>Mode</th><td>" + (strict ? "STRICT (unknown fields fail)" : "LENIENT (unknown fields warn)")
                + "</td></tr>"
                + "<tr><th>Violations</th><td>" + violations.size() + "</td></tr>"
                + "<tr><th>Unknown fields</th><td>" + drift.size() + "</td></tr>"
                + "<tr><th>Result</th><td class='" + (failed ? "fail" : "pass") + "'>"
                + (failed ? "FAIL" : "PASS") + "</td></tr>"
                + "</table>"
                + "<table><tr><th>Kind</th><th>Location</th><th>Detail</th></tr>" + rows + "</table>"
                + "</body></html>";
        Allure.addAttachment(checkTitle, "text/html", html);
    }

    private static String row(String kind, String cssClass, ValidationMessage message) {
        return "<tr><td class='" + cssClass + "'>" + escapeHtml(kind) + "</td><td>"
                + escapeHtml(String.valueOf(message.getInstanceLocation())) + "</td><td>"
                + escapeHtml(message.getMessage()) + "</td></tr>";
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
