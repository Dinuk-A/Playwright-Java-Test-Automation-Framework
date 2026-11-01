package com.dinuka.automation.utils.api.playwright;

import com.microsoft.playwright.APIResponse;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

public final class PWAssertionUtils {

    private PWAssertionUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // --------------------- Status Code & Headers ---------------------

    /**
     * Verifies that the actual HTTP status code matches the expected one.
     */
    public static void assertStatusCode(APIResponse response, int expectedCode) {
        Assert.assertEquals(
                response.status(),
                expectedCode,
                "Status code is not as expected."
        );
    }

    /**
     * Verifies the response content type.
     */
    public static void assertContentType(APIResponse response, String expectedContentType) {
        String actualContentType = response.headers().get("content-type");

        Assert.assertTrue(
                actualContentType != null && actualContentType.contains(expectedContentType),
                String.format(
                        "Expected Content-Type: %s but got: %s",
                        expectedContentType,
                        actualContentType
                )
        );
    }

    // --------------------- JSON Assertions ---------------------

    /**
     * Verifies that a JSON field matches the expected value.
     */
    public static void assertJsonFieldEquals(
            APIResponse response,
            String jsonPath,
            Object expectedValue
    ) {
        Object actualValue = getJsonValue(getResponseBody(response), jsonPath);

        Assert.assertEquals(
                actualValue,
                expectedValue,
                String.format(
                        "Expected value at '%s' does not match.",
                        jsonPath
                )
        );
    }

    /**
     * Verifies that a JSON field is not null.
     */
    public static void assertJsonFieldNotNull(APIResponse response, String jsonPath) {
        Object value = getJsonValue(getResponseBody(response), jsonPath);

        Assert.assertNotNull(
                value,
                String.format("Expected field '%s' to be not null.", jsonPath)
        );
    }

    /**
     * Verifies that a JSON field is null.
     */
    public static void assertJsonFieldIsNull(String rawJson, String jsonPath) {
        Object value = getJsonValue(rawJson, jsonPath);

        Assert.assertNull(
                value,
                String.format(
                        "Expected field '%s' to be null, but it was: %s",
                        jsonPath,
                        value
                )
        );
    }

    /**
     * Verifies the type of a JSON field.
     */
    public static void assertJsonFieldType(
            String rawJson,
            String jsonPath,
            Class<?> expectedClass
    ) {
        Object actualValue = getJsonValue(rawJson, jsonPath);

        Assert.assertNotNull(
                actualValue,
                String.format("Field at '%s' is null.", jsonPath)
        );

        Assert.assertTrue(
                expectedClass.isInstance(actualValue),
                String.format(
                        "Expected type %s but got %s for field '%s'.",
                        expectedClass.getSimpleName(),
                        actualValue.getClass().getSimpleName(),
                        jsonPath
                )
        );
    }

    // --------------------- Response Body Assertions ---------------------

    /**
     * Verifies that the response body contains expected text.
     */
    public static void assertResponseBodyContainsText(
            APIResponse response,
            String expectedText
    ) {
        assertResponseBodyContains(getResponseBody(response), expectedText);
    }

    /**
     * Verifies that a response body contains expected text.
     */
    public static void assertResponseBodyContains(
            String actualBody,
            String expectedText
    ) {
        Assert.assertTrue(
                actualBody.contains(expectedText),
                String.format(
                        "Response body does not contain expected text: %s",
                        expectedText
                )
        );
    }

    /**
     * Verifies that the response body is empty.
     */
    public static void assertResponseBodyIsEmpty(String responseBody) {
        String trimmed = responseBody.trim();

        Assert.assertTrue(
                trimmed.isEmpty()
                        || "[]".equals(trimmed)
                        || "{}".equals(trimmed),
                "Response body is not empty. Actual body: " + trimmed
        );
    }

    // --------------------- Response Time ---------------------

    /**
     * Verifies that response time is within the allowed limit.
     */
    public static void assertResponseTime(long responseTime, long maxTime) {
        Assert.assertTrue(
                responseTime < maxTime,
                String.format(
                        "Response time exceeded limit. Actual: %dms, Maximum: %dms",
                        responseTime,
                        maxTime
                )
        );
    }

    // --------------------- Private Helper Methods ---------------------

    /**
     * Extracts response body as String.
     */
    private static String getResponseBody(APIResponse response) {
        return new String(response.body());
    }

    /**
     * Extracts JSON value using JSON path.
     */
    private static Object getJsonValue(String rawJson, String jsonPath) {
        return JsonPath.from(rawJson).get(jsonPath);
    }
}