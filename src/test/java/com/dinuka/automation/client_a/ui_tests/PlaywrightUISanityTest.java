package com.dinuka.automation.client_a.ui_tests;

import com.dinuka.automation.utils.ui.core.PlaywrightBaseTest;
import com.dinuka.automation.utils.ui.playwright.ElementActions;
import com.dinuka.automation.utils.ui.playwright.UiAssertionUtils;
import com.dinuka.automation.utils.ui.playwright.WaitUtils;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Sanity checks for the Playwright UI stack: Playwright + TestNG +
 * PlaywrightBaseTest + ElementActions/WaitUtils/UiAssertionUtils.
 *
 * Runs entirely against the-internet.herokuapp.com, a public site built
 * specifically for browser-automation practice, so these tests validate
 * ONLY that the Playwright plumbing itself is healthy (browser launch,
 * navigation, locators, waits, assertions) - not any part of the actual
 * e-commerce framework.
 */
@Epic("Framework Sanity Checks")
public class PlaywrightUISanityTest extends PlaywrightBaseTest {

    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @Test(groups = { "sanity" })
    @Story("Playwright UI - Navigation & Assertions")
    @Description("Verify Playwright can launch a browser, navigate, and read page content")
    @Severity(SeverityLevel.BLOCKER)
    public void testHomePageLoadsAndLinksArePresent() {
        page.navigate(BASE_URL);
        WaitUtils.waitForPageLoad(page);

        UiAssertionUtils.assertPageTitle(page, "The Internet");

        Locator heading = page.locator("h1");
        UiAssertionUtils.assertElementVisible(heading);
        UiAssertionUtils.assertElementContainsText(heading, "Welcome to the-internet");

        int linkCount = page.locator("#content ul li a").count();
        Assert.assertTrue(linkCount > 0, "Expected the example-links list to contain at least one link");
    }

    @Test(groups = { "sanity" })
    @Story("Playwright UI - Form Input & Click")
    @Description("Verify ElementActions can type into fields, click, and the resulting page state is correct")
    @Severity(SeverityLevel.BLOCKER)
    public void testFormAuthenticationHappyPathLogsIn() {
        ElementActions actions = new ElementActions(page);

        page.navigate(BASE_URL + "/login");
        WaitUtils.waitForPageLoad(page);

        actions.type(page.locator("#username"), "tomsmith");
        actions.type(page.locator("#password"), "SuperSecretPassword!");
        actions.click(page.locator("button[type='submit']"));

        WaitUtils.waitForElementVisible(page.locator("#flash"));
        UiAssertionUtils.assertElementContainsText(page.locator("#flash"), "You logged into a secure area!");
        UiAssertionUtils.assertUrlContains(page, "/secure");
    }

    @Test(groups = { "sanity" })
    @Story("Playwright UI - Dynamic Waits")
    @Description("Verify WaitUtils correctly waits for an element that only appears after an async delay")
    @Severity(SeverityLevel.CRITICAL)
    public void testDynamicLoadingRevealsHiddenText() {
        page.navigate(BASE_URL + "/dynamic_loading/1");
        WaitUtils.waitForPageLoad(page);

        Locator startButton = page.locator("#start button");
        UiAssertionUtils.assertElementVisible(startButton);
        startButton.click();

        Locator finishText = WaitUtils.waitForElementVisible(page.locator("#finish h4"), 15000);
        UiAssertionUtils.assertElementTextEquals(finishText, "Hello World!");
    }
}