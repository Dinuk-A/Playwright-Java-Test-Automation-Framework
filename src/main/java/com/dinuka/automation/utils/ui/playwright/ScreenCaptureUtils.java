package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ScreenCaptureUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScreenCaptureUtils.class);
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    public static final String SCREENSHOT_FOLDER = Paths.get(System.getProperty("user.dir"), "screenshots").toString();

    private ScreenCaptureUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static String timestamp() {
        return LocalDateTime.now(ZoneId.systemDefault()).format(TS_FORMAT);
    }

    public static String takePageScreenshot(Page page, String namePrefix) {
        try {
            String filename = String.format("%s_%s.png", namePrefix, timestamp());
            File dest = new File(SCREENSHOT_FOLDER, filename);
            FileUtils.forceMkdirParent(dest);
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(dest.getAbsolutePath())).setFullPage(true));
            return dest.getAbsolutePath();
        } catch (PlaywrightException | IOException e) {
            logger.error("Failed to take page screenshot: {}", e.getMessage(), e);
            return "";
        }
    }

    public static String takeViewportScreenshot(Page page, String namePrefix) {
        try {
            String filename = String.format("%s_viewport_%s.png", namePrefix, timestamp());
            File dest = new File(SCREENSHOT_FOLDER, filename);
            FileUtils.forceMkdirParent(dest);
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(dest.getAbsolutePath())).setFullPage(false));
            return dest.getAbsolutePath();
        } catch (PlaywrightException | IOException e) {
            logger.error("Failed to take viewport screenshot: {}", e.getMessage(), e);
            return "";
        }
    }

    public static String takeElementScreenshot(Locator locator, String namePrefix) {
        try {
            String filename = String.format("%s_element_%s.png", namePrefix, timestamp());
            File dest = new File(SCREENSHOT_FOLDER, filename);
            FileUtils.forceMkdirParent(dest);
            locator.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(dest.getAbsolutePath())));
            return dest.getAbsolutePath();
        } catch (PlaywrightException | IOException e) {
            logger.error("Failed to take element screenshot: {}", e.getMessage(), e);
            return "";
        }
    }

    public static byte[] takePageScreenshotAsBytes(Page page) {
        try {
            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        } catch (PlaywrightException e) {
            logger.error("Failed to take page screenshot as bytes: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    public static byte[] takeViewportScreenshotAsBytes(Page page) {
        try {
            return page.screenshot(new Page.ScreenshotOptions().setFullPage(false));
        } catch (PlaywrightException e) {
            logger.error("Failed to take viewport screenshot as bytes: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    public static byte[] takeElementScreenshotAsBytes(Locator locator) {
        try {
            return locator.screenshot();
        } catch (PlaywrightException e) {
            logger.error("Failed to take element screenshot as bytes: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    public static String takePageScreenshotWithOptions(Page page, String namePrefix,
            int quality, String type) {
        try {
            String filename = String.format("%s_%s.%s", namePrefix, timestamp(), type);
            File dest = new File(SCREENSHOT_FOLDER, filename);
            FileUtils.forceMkdirParent(dest);
            Page.ScreenshotOptions options = new Page.ScreenshotOptions()
                    .setPath(Paths.get(dest.getAbsolutePath()))
                    .setFullPage(true)
                    .setQuality(quality);
            if ("png".equalsIgnoreCase(type) || "jpeg".equalsIgnoreCase(type)) {
                page.screenshot(options);
            }
            return dest.getAbsolutePath();
        } catch (PlaywrightException | IOException e) {
            logger.error("Failed to take page screenshot with options: {}", e.getMessage(), e);
            return "";
        }
    }

    public static String takeClipRegionScreenshot(Page page, String namePrefix,
            double x, double y, double width, double height) {
        try {
            String filename = String.format("%s_clip_%s.png", namePrefix, timestamp());
            File dest = new File(SCREENSHOT_FOLDER, filename);
            FileUtils.forceMkdirParent(dest);
            Page.ScreenshotOptions options = new Page.ScreenshotOptions()
                    .setPath(Paths.get(dest.getAbsolutePath()))
                    .setClip(x, y, width, height);
            page.screenshot(options);
            return dest.getAbsolutePath();
        } catch (PlaywrightException | IOException e) {
            logger.error("Failed to take clip region screenshot: {}", e.getMessage(), e);
            return "";
        }
    }

    public static String takeScreenshotWithMask(Page page, String namePrefix, Locator maskLocator) {
        try {
            String filename = String.format("%s_masked_%s.png", namePrefix, timestamp());
            File dest = new File(SCREENSHOT_FOLDER, filename);
            FileUtils.forceMkdirParent(dest);
            Page.ScreenshotOptions options = new Page.ScreenshotOptions()
                    .setPath(Paths.get(dest.getAbsolutePath()))
                    .setFullPage(true)
                    .setMask(java.util.List.of(maskLocator));
            page.screenshot(options);
            return dest.getAbsolutePath();
        } catch (PlaywrightException | IOException e) {
            logger.error("Failed to take screenshot with mask: {}", e.getMessage(), e);
            return "";
        }
    }

    public static String takePagePdf(Page page, String namePrefix) {
        try {
            String filename = String.format("%s_%s.pdf", namePrefix, timestamp());
            File dest = new File(SCREENSHOT_FOLDER, filename);
            FileUtils.forceMkdirParent(dest);
            page.pdf(new Page.PdfOptions().setPath(Paths.get(dest.getAbsolutePath())));
            return dest.getAbsolutePath();
        } catch (PlaywrightException | IOException e) {
            logger.error("Failed to take page PDF: {}", e.getMessage(), e);
            return "";
        }
    }

    public static boolean compareScreenshots(String screenshotPath1, String screenshotPath2) {
        try {
            byte[] file1 = Files.readAllBytes(Paths.get(screenshotPath1));
            byte[] file2 = Files.readAllBytes(Paths.get(screenshotPath2));
            return java.util.Arrays.equals(file1, file2);
        } catch (IOException e) {
            logger.error("Failed to compare screenshots: {}", e.getMessage(), e);
            return false;
        }
    }

    public static void initializeScreenshotFolder() {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_FOLDER));
        } catch (IOException e) {
            logger.error("Failed to initialize screenshot folder: {}", e.getMessage(), e);
        }
    }

    public static String takePageScreenshotAsBase64(Page page) {
        byte[] bytes = takePageScreenshotAsBytes(page); // reuse existing method
        return bytes.length > 0 ? Base64.getEncoder().encodeToString(bytes) : null;
    }
}