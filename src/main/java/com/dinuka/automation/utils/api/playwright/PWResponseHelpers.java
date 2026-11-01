package com.dinuka.automation.utils.api.playwright;

import java.io.IOException;
import java.util.Map;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.APIRequestContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class PWResponseHelpers {
    private static final Logger logger = LoggerFactory.getLogger(PWResponseHelpers.class);

    // for JSON parsing in getJsonNode
    private static final ObjectMapper mapper = new ObjectMapper();

    private APIRequestContext apiContext;
    private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds

    // Constructor - initialize API context
    public PWResponseHelpers(APIRequestContext apiContext) {
        this.apiContext = apiContext;
    }

    // ========================================================================
    // RESPONSE HELPERS
    // ========================================================================

    /*
     * Get response status code
     */
    public static int getStatusCode(APIResponse response) {
        return response.status();
    }

    /*
     * Get response body as string
     */
    public static String getResponseBody(APIResponse response) {
        return response.text();
    }

    /*
     * Get response body as JSON object (requires JsonPath or custom parsing)
     */
    public static String getResponseAsJson(APIResponse response) {
        return response.text();
    }

    /*
     * Get specific header from response
     */
    public static String getResponseHeader(APIResponse response, String headerName) {
        return response.headers().get(headerName);
    }

    /*
     * Check if response is OK (status 200-299)
     */
    public static boolean isResponseOk(APIResponse response) {
        return response.ok();
    }

    /*
     * Get all response headers as map
     */
    public static Map<String, String> getResponseHeaders(APIResponse response) {
        return response.headers();
    }

    /*
     * Verify response status code matches expected
     */
    public static boolean verifyStatusCode(APIResponse response, int expectedStatusCode) {
        return response.status() == expectedStatusCode;
    }

    /*
     * Close API context (cleanup)
     */
    public void closeApiContext() {
        try {
            if (apiContext != null) {
                apiContext.dispose();
            }
        } catch (PlaywrightException e) {
            logger.info("Error closing API context: " + e.getMessage());
        }
    }

    /*
     * Parse response body to JSON node
     */
    public static JsonNode getJsonNode(APIResponse response) {
        try {
            return mapper.readTree(response.body());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse response JSON", e);
        }
    }
}
