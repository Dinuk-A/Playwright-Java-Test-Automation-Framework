package com.dinuka.automation.utils.api.playwright;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.Map;

public class PWAPIBaseTest {
    protected Playwright playwright;
    protected APIRequestContext request;

    @BeforeClass(alwaysRun = true)
    public void initPlaywright() {
        playwright = Playwright.create();
    }

    protected void createApiContext(String baseUrl) {
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(baseUrl)
                        .setExtraHTTPHeaders(Map.of(
                                "Content-Type", "application/json",
                                "Accept", "application/json")));
    }

    @AfterClass(alwaysRun = true)
    public void teardownApi() {
        if (request != null) {
            request.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
