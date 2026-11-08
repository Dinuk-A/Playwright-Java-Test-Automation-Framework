package com.dinuka.automation.client_a.api_tests;

import com.dinuka.automation.utils.api.playwright.PWAPIBaseTest;
import com.dinuka.automation.utils.api.playwright.PWAPIUtils;
import com.dinuka.automation.utils.api.playwright.PWAssertionUtils;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Sanity checks for the Playwright API stack: Playwright's
 * APIRequestContext + PWAPIBaseTest + PWAPIUtils/PWAssertionUtils.
 *
 * Runs against jsonplaceholder.typicode.com, a free public fake-REST-API
 * used for prototyping - no project-specific dependencies.
 */
@Epic("Framework Sanity Checks")
public class PlaywrightAPISanityTest extends PWAPIBaseTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private PWAPIUtils api;

    @BeforeMethod(alwaysRun = true)
    public void setupRequest() {
        createApiContext(BASE_URL);
        api = new PWAPIUtils(request);
    }

    @Test(groups = { "sanity" })
    @Story("Playwright API - GET")
    @Description("Verify a basic GET request returns 200 and the expected JSON shape")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetSinglePostReturnsExpectedFields() {
        APIResponse response = api.get("/posts/1");

        PWAssertionUtils.assertStatusCode(response, 200);
        PWAssertionUtils.assertContentType(response, "application/json");
        PWAssertionUtils.assertJsonFieldEquals(response, "id", 1);
        PWAssertionUtils.assertJsonFieldNotNull(response, "title");
        PWAssertionUtils.assertJsonFieldNotNull(response, "body");
    }

    @Test(groups = { "sanity" })
    @Story("Playwright API - POST")
    @Description("Verify a POST request with a JSON body is accepted and echoed back")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePostEchoesSubmittedBody() {
        String requestBody = "{\"title\":\"sanity check\",\"body\":\"validating the Playwright API stack\",\"userId\":1}";

        APIResponse response = api.postJson("/posts", requestBody);

        PWAssertionUtils.assertStatusCode(response, 201);
        PWAssertionUtils.assertJsonFieldEquals(response, "title", "sanity check");
        PWAssertionUtils.assertJsonFieldNotNull(response, "id");
    }

    @Test(groups = { "sanity" })
    @Story("Playwright API - Not Found")
    @Description("Verify requesting a non-existent resource returns 404, not a silent 200/500")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentPostReturns404() {
        APIResponse response = api.get("/posts/999999");

        PWAssertionUtils.assertStatusCode(response, 404);
    }
}