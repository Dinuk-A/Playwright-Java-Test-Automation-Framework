package com.dinuka.automation.utils.ui.selenium;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.time.format.DateTimeFormatter;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ScreenCaptureUtils {

    private static final DateTimeFormatter timeStampFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    public static final String screenshotFolder = Paths.get(System.getProperty("user.dir"), "screenshots").toString();

    private ScreenCaptureUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static String timestamp() {
        return LocalDateTime.now(ZoneId.systemDefault()).format(timeStampFormat);
    }

    // Takes a screenshot of the entire page and saves it with a timestamped filename.
    public static String takePageScreenshot(WebDriver driver, String namePrefix) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String filename = String.format("%s_%s.png", namePrefix, timestamp());
            File dest = new File(screenshotFolder, filename);
            FileUtils.forceMkdirParent(dest);
            FileUtils.copyFile(src, dest);
            return dest.getAbsolutePath();
        } catch (IOException | WebDriverException e) {
            e.printStackTrace();
            return null;
        }

    }

    // Takes a screenshot of a specific WebElement and saves it with a timestamped filename.
    public static String takeElementScreenshot(WebElement element, String namePrefix) {
        try {
            File src = element.getScreenshotAs(OutputType.FILE);
            String filename = String.format("%s_element_%s.png", namePrefix, timestamp());
            File dest = new File(screenshotFolder, filename);
            FileUtils.forceMkdirParent(dest);
            FileUtils.copyFile(src, dest);
            return dest.getAbsolutePath();
        } catch (IOException | WebDriverException e) {
            e.printStackTrace();
            return null;
        }
    }
}
