package com.dinuka.automation.utils.api.restassured;

import org.testng.Assert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class AssertionUtils {

      private AssertionUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // --------------------- Status Code & Headers ---------------------

    /**
     * Verifies that the actual HTTP status code matches the expected one.
     *
     * Use this to assert if the request was successful, failed, unauthorized, etc.
     * response - The HTTP response to check.
     * expectedCode - The expected HTTP status code (e.g., 200, 404).
     */
    public static void assertStatusCode(Response response, int expectedCode) {
        int actualCode = response.getStatusCode();
        Assert.assertEquals(actualCode, expectedCode, "Status code is not as expected.");
    }

    /**
     * Checks if the response Content-Type header matches the expected value.
     *
     * Use this to confirm the response format (JSON, XML, plain text, etc.).
     * response - The HTTP response to check.
     * expectedContentType - The expected content type (e.g., application/json).
     */
    public static void assertContentType(Response response, String expectedContentType) {
        String actualContentType = response.getHeader("Content-Type");
        Assert.assertTrue(actualContentType.contains(expectedContentType),
                "Expected Content-Type: " + expectedContentType + " but got: " + actualContentType);
    }

    // --------------------- Response Body (JSON) ---------------------

    /**
     * Checks if a specific JSON field in the response matches the expected value.
     *
     * Use this for validating specific fields in the JSON response.
     * response - The HTTP response to check.
     * jsonPath - The JSON path to the field (e.g., $.data.id).
     * expectedValue - The expected value of the field.
     */
    public static void assertJsonFieldEquals(Response response, String jsonPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(jsonPath);
        Assert.assertEquals(actualValue, expectedValue, "Expected value at '" + jsonPath + "' does not match.");
    }

    /**
     * Checks if a specific JSON field in the response is not null.
     *
     * Use this for validating that a field exists and has a value.
     * response - The HTTP response to check.
     * jsonPath - The JSON path to the field (e.g., $.data.id).
     */
    public static void assertJsonFieldNotNull(Response response, String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        Assert.assertNotNull(value, "Expected field '" + jsonPath + "' to be not null.");
    }

    // --------------------- Response Body (Text) ---------------------

    /**
     * Checks if the response body contains a specific expected text.
     *
     * Use this for validating presence of specific keywords, messages, or JSON
     * fragments.
     * response - The HTTP response to check.
     * expectedText - The substring that should be present in the response.
     */
    public static void assertResponseBodyContainsText(Response response, String expectedText) {
        String actualBody = response.getBody().asString();
        Assert.assertTrue(actualBody.contains(expectedText),
                "Response body does not contain expected text: " + expectedText);
    }

    /**
     * Checks whether the response body contains a specific expected text.
     *
     * Use this for validating presence of specific keywords, messages, or JSON
     * fragments.
     * actualBody - The full response body as a string.
     * expectedText - The substring that should be present in the response.
     */
    public static void assertResponseBodyContains(String actualBody, String expectedText) {
        Assert.assertTrue(actualBody.contains(expectedText),
                "Response body does not contain the expected text.");
    }

    // --------------------- Response Time ---------------------

    /**
     * Verifies that the API response time is under the specified maximum time.
     *
     * Use this to ensure performance SLAs or detect slow endpoints.
     * responseTime - The actual response time in milliseconds.
     * maxTime - The maximum expected response time in milliseconds.
     */
    public static void assertResponseTime(long responseTime, long maxTime) {
        Assert.assertTrue(responseTime < maxTime,
                "Response time exceeded the maximum allowed time. Actual time: " + responseTime + "ms");
    }

    // --------------------- Empty Body Check ---------------------

    /**
     * Asserts that the response body is empty.
     *
     * Use this for requests like DELETE or HEAD, where an empty body is expected.
     * actualBody - The actual body of the response.
     */
    public static void assertResponseBodyIsEmptyOri(String actualBody) {
        Assert.assertTrue(actualBody.isEmpty(), "Response body is not empty.");
    }

    //new
    public static void assertResponseBodyIsEmpty(String responseBody) {
        String trimmed = responseBody.trim();
        if (!trimmed.equals("[]") && !trimmed.equals("{}") && !trimmed.isEmpty()) {
            Assert.fail("Response body is not empty. Actual body: " + trimmed);
        }
    }
    

    /**
     * Asserts that a JSON field in a raw JSON string is of the expected type.
     *
     * @param rawJson       Raw JSON string.
     * @param jsonPath      JSON path to the field.
     * @param expectedClass Expected Java class.
     */
    public static void assertJsonFieldType(String rawJson, String jsonPath, Class<?> expectedClass) {
        Object actualValue = JsonPath.from(rawJson).get(jsonPath);
        Assert.assertNotNull(actualValue, "Field at '" + jsonPath + "' is null.");
        Assert.assertTrue(expectedClass.isInstance(actualValue),
                "Expected type " + expectedClass.getSimpleName() +
                        " but got " + actualValue.getClass().getSimpleName() + " for field '" + jsonPath + "'.");
    }

    /**
     * Asserts that a JSON field exists and its value is explicitly null.
     *
     * @param rawJson  The raw JSON string.
     * @param jsonPath The JSON path to the field.
     */
    public static void assertJsonFieldIsNull(String rawJson, String jsonPath) {
        Object value = JsonPath.from(rawJson).get(jsonPath);
        Assert.assertNull(value, "Expected field '" + jsonPath + "' to be null, but it was: " + value);
    }

}
