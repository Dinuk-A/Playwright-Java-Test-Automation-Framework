package com.dinuka.automation.client_a.api_tests;

import com.dinuka.automation.utils.api.restassured.ApiUtils;
import com.dinuka.automation.utils.api.restassured.AssertionUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Sanity checks for the REST Assured API stack: REST Assured +
 * ApiUtils/AssertionUtils.
 *
 * Runs against jsonplaceholder.typicode.com - same public API used by the
 * Playwright API sanity suite, so both stacks can be compared side by side.
 */
@Epic("Framework Sanity Checks")
public class RestAssuredAPISanityTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @Test(groups = { "sanity" })
    @Story("REST Assured API - GET")
    @Description("Verify a basic GET request returns 200 and the expected JSON field values")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetSingleUserReturnsExpectedFields() {
        Response response = ApiUtils.getRequest(BASE_URL, "/users/1");

        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertContentType(response, "application/json");
        AssertionUtils.assertJsonFieldEquals(response, "id", 1);
        AssertionUtils.assertJsonFieldNotNull(response, "email");
        AssertionUtils.assertJsonFieldNotNull(response, "username");
    }

    @Test(groups = { "sanity" })
    @Story("REST Assured API - Query Params")
    @Description("Verify a GET with query parameters filters results correctly")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPostsFilteredByUserId() {
        Response response = ApiUtils.getReqWithQueryParams(BASE_URL, "/posts", Map.of("userId", "1"));

        AssertionUtils.assertStatusCode(response, 200);

        List<Integer> returnedUserIds = response.jsonPath().getList("userId", Integer.class);
        Assert.assertFalse(returnedUserIds.isEmpty(), "Expected at least one post for userId=1");
        Assert.assertEquals(returnedUserIds.stream().distinct().count(), 1,
                "Expected every returned post to belong to userId=1");
    }

    @Test(groups = { "sanity" })
    @Story("REST Assured API - Not Found")
    @Description("Verify requesting a non-existent resource returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentUserReturns404() {
        Response response = ApiUtils.getRequest(BASE_URL, "/users/999999");

        AssertionUtils.assertStatusCode(response, 404);
    }
}