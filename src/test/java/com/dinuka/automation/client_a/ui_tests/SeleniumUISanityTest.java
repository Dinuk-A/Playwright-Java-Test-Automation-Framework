package com.dinuka.automation.client_a.ui_tests;


import com.dinuka.automation.utils.ui.core.SeleniumBaseTest;
import com.dinuka.automation.utils.ui.selenium.BrowserUtils;
import com.dinuka.automation.utils.ui.selenium.ElementActions;
import com.dinuka.automation.utils.ui.selenium.UiAssertionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Sanity checks for the Selenium UI stack: Selenium WebDriver + TestNG +
 * SeleniumBaseTest + ElementActions/UiAssertionUtils.
 *
 * Same target site as the Playwright UI sanity suite
 * (the-internet.herokuapp.com), so the two can be compared side by side to
 * confirm both drivers behave consistently.
 *
 * NOTE: SeleniumBaseTest#setUp(browser, headless) requires TestNG suite
 * parameters "browser" and "headless" - unlike PlaywrightBaseTest these are
 * NOT optional/defaulted, so the suite XML must supply them (see the
 * accompanying suite file).
 */
public class SeleniumUISanityTest extends SeleniumBaseTest {

    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @Test(groups = { "sanity" })
    public void testHomePageLoadsAndLinksArePresent() {
        BrowserUtils.openUrl(driver, BASE_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));

        UiAssertionUtils.assertPageTitle(driver, "The Internet");

        WebElement heading = driver.findElement(By.tagName("h1"));
        UiAssertionUtils.assertElementVisible(heading);
        UiAssertionUtils.assertElementContainsText(heading, "Welcome to the-internet");

        Assert.assertTrue(driver.findElements(By.cssSelector("#content ul li a")).size() > 0,
                "Expected the example-links list to contain at least one link");
    }

    @Test(groups = { "sanity" })
    public void testFormAuthenticationHappyPathLogsIn() {
        ElementActions actions = new ElementActions(driver);

        BrowserUtils.openUrl(driver, BASE_URL + "/login");

        actions.type(driver.findElement(By.id("username")), "tomsmith");
        actions.type(driver.findElement(By.id("password")), "SuperSecretPassword!");
        actions.click(driver.findElement(By.cssSelector("button[type='submit']")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
        WebElement flash = driver.findElement(By.id("flash"));
        UiAssertionUtils.assertElementContainsText(flash, "You logged into a secure area!");
        UiAssertionUtils.assertUrlContains(driver, "/secure");
    }

    @Test(groups = { "sanity" })
    public void testCheckboxesCanBeToggled() {
        BrowserUtils.openUrl(driver, BASE_URL + "/checkboxes");

        WebElement firstCheckbox = driver.findElement(
                By.cssSelector("#checkboxes input[type='checkbox']:nth-of-type(1)"));
        boolean initialState = firstCheckbox.isSelected();

        firstCheckbox.click();

        Assert.assertNotEquals(firstCheckbox.isSelected(), initialState,
                "Expected the checkbox state to flip after clicking it");
    }
}