
package com.dinuka.automation.utils.ui.core;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightBaseTest {

    // ThreadLocal to store Playwright instances per thread
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    // Initialize Playwright and browser before each test class
    @BeforeClass
    @Parameters({ "browser", "headless" })
    public void setupPlaywright(
            @Optional("") String browser,
            @Optional("") String headless) {

        // Priority: TestNG XML -> Properties -> Hardcoded fallback
        String selectedBrowser = (browser != null && !browser.isBlank())
                ? browser
                : PlaywrightConfig.getBrowser();

        boolean selectedHeadless = (headless != null && !headless.isBlank())
                ? Boolean.parseBoolean(headless)
                : PlaywrightConfig.isHeadless();

        playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);

        // Configure browser launch options
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(selectedHeadless)
                .setSlowMo(PlaywrightConfig.getSlowMo());

        // Launch the selected browser
        switch (selectedBrowser.toLowerCase()) {

            case "firefox":
                this.browser = playwright.firefox().launch(launchOptions);
                break;

            case "webkit":
                this.browser = playwright.webkit().launch(launchOptions);
                break;

            case "chromium":
            default:
                this.browser = playwright.chromium().launch(launchOptions);
                break;
        }

        browserThreadLocal.set(this.browser);
    }

    // Create a fresh browser context and page before each test method
    @BeforeMethod
    public void createContextAndPage() {

        Browser currentBrowser = browserThreadLocal.get();

        if (currentBrowser == null) {
            throw new IllegalStateException(
                    "Browser is not initialized for the current thread.");
        }

        // Create an isolated browser context
        BrowserContext newContext = currentBrowser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(
                                PlaywrightConfig.getViewportWidth(),
                                PlaywrightConfig.getViewportHeight()));

        Page newPage = newContext.newPage();

        // Configure page timeouts
        newPage.setDefaultTimeout(
                PlaywrightConfig.getDefaultTimeout());

        newPage.setDefaultNavigationTimeout(
                PlaywrightConfig.getNavigationTimeout());

        // Store context and page in ThreadLocal
        contextThreadLocal.set(newContext);
        pageThreadLocal.set(newPage);

        // Maintain instance variables for backward compatibility
        this.context = newContext;
        this.page = newPage;
    }

    // Close browser context after each test method
    @AfterMethod(alwaysRun = true)
    public void closeContext() {

        BrowserContext currentContext = contextThreadLocal.get();

        if (currentContext != null) {
            try {
                currentContext.close();
            } finally {
                contextThreadLocal.remove();
            }
        }

        pageThreadLocal.remove();

        this.context = null;
        this.page = null;
    }

    // Close browser and Playwright after all tests in the class
    @AfterClass(alwaysRun = true)
    public void teardownPlaywright() {

        Browser currentBrowser = browserThreadLocal.get();

        if (currentBrowser != null) {
            try {
                currentBrowser.close();
            } finally {
                browserThreadLocal.remove();
            }
        }

        Playwright currentPlaywright = playwrightThreadLocal.get();

        if (currentPlaywright != null) {
            try {
                currentPlaywright.close();
            } finally {
                playwrightThreadLocal.remove();
            }
        }

        this.browser = null;
        this.playwright = null;
    }
}
