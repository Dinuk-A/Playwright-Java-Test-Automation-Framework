package com.dinuka.automation.utils.ui.core;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.openqa.selenium.Cookie;
import java.io.File;
import java.util.Date;
import java.util.List;

public class SeleniumBaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    public static final String HEADLESSNEW = "--headless=new";
    public static final String STARTMAXIMIZED = "--start-maximized";
    public static final String DISABLEGPU = "--disable-gpu";
    public static final String NOSANDBOX = "--no-sandbox";

    // Initialize WebDriver before each test method
    @BeforeMethod
    @Parameters({ "browser", "headless" })
    public void setUp(String browser, String headless) {

        boolean isHeadless = headless.equalsIgnoreCase("true");

        switch (browser.toLowerCase()) {
            case "chrome":
                driver = initializeChromeDriver(isHeadless);
                break;
            case "firefox":
                driver = initializeFirefoxDriver(isHeadless);
                break;
            case "edge":
                driver = initializeEdgeDriver(isHeadless);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);

        }

        // Set global implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Create an explicit wait object for dynamic waits
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Initialize ChromeDriver
    private WebDriver initializeChromeDriver(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();

        options.addArguments(STARTMAXIMIZED);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        if (isHeadless) {
            options.addArguments(HEADLESSNEW);
            options.addArguments(DISABLEGPU);
            options.addArguments(NOSANDBOX);
        }

        return new ChromeDriver(options);
    }

    // Initialize EdgeDriver
    private WebDriver initializeEdgeDriver(boolean isHeadless) {
        EdgeOptions options = new EdgeOptions();
        if (isHeadless) {
            options.addArguments(HEADLESSNEW);
            options.addArguments(DISABLEGPU);
            options.addArguments(NOSANDBOX);
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments(STARTMAXIMIZED);
        return new EdgeDriver(options);
    }

    // Initialize FirefoxDriver
    private WebDriver initializeFirefoxDriver(boolean isHeadless) {
        FirefoxOptions options = new FirefoxOptions();
        if (isHeadless) {
            options.addArguments("-headless");
        }
        options.addArguments(STARTMAXIMIZED);
        return new FirefoxDriver(options);
    }

    // Initialize ChromeDriver with a specific user profile
    @SuppressWarnings("unused")
    private WebDriver initializeChromeDriverWithNewProfile(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();

        options.addArguments(STARTMAXIMIZED);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        options.addArguments("user-data-dir=C:\\Users\\Dinuka Pramod\\AppData\\Local\\Google\\Chrome\\User Data");
        options.addArguments("profile-directory=Profile 2");

        if (isHeadless) {
            // running with a real profile + headless usually doesn’t work well
            options.addArguments(HEADLESSNEW);
            options.addArguments(DISABLEGPU);
            options.addArguments(NOSANDBOX);
        }

        return new ChromeDriver(options);
    }

    // Load cookies from a JSON file and add them to the current browser session
    public void loadCookiesFromFile(String cookiesFilePath, String domainUrl) throws Exception {
        // Open the domain once
        driver.get(domainUrl);

        // Read cookies.json
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> cookiesList = mapper.readValue(new File(cookiesFilePath), List.class);

        for (Map<String, Object> c : cookiesList) {
            String name = (String) c.get("name");
            String value = (String) c.get("value");
            String domain = (String) c.get("domain");
            String path = (String) c.get("path");
            boolean isSecure = Boolean.TRUE.equals(c.get("secure"));
            boolean isHttpOnly = Boolean.TRUE.equals(c.get("httpOnly"));
            Date expiry = null;
            if (c.get("expiry") != null) {
                long epoch = ((Number) c.get("expiry")).longValue() * 1000L;
                expiry = new Date(epoch);
            }

            Cookie cookie = new Cookie.Builder(name, value)
                    .domain(domain)
                    .path(path)
                    .isSecure(isSecure)
                    .isHttpOnly(isHttpOnly)
                    .expiresOn(expiry)
                    .build();

            driver.manage().addCookie(cookie);
        }

        // Refresh page to apply cookies
        driver.navigate().refresh();
    }

    // Close the browser and quit the WebDriver session
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

}
