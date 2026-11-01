package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.*;
import java.util.List;

public class BrowserUtils {

    private BrowserUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void openUrl(Page page, String url) {
        page.navigate(url);
    }

    public static void goBack(Page page) {
        page.goBack();
    }

    public static void goForward(Page page) {
        page.goForward();
    }

    public static void refreshPage(Page page) {
        page.reload();
    }

    public static String getCurrentUrl(Page page) {
        return page.url();
    }

    public static String getPageTitle(Page page) {
        return page.title();
    }

    public static void maximizeWindow(Page page) {
        page.setViewportSize(1920, 1080);
    }

    public static void fullscreenWindow(Page page) {
        page.setViewportSize(1920, 1440);
    }

    public static void switchToWindowByIndex(BrowserContext context, int index) {
        List<Page> pages = context.pages();
        if (index >= 0 && index < pages.size()) {
            pages.get(index).bringToFront();
        }
    }

    public static List<Page> getAllPages(BrowserContext context) {
        return context.pages();
    }

    public static Page getCurrentPage(BrowserContext context) {
        List<Page> pages = context.pages();
        return pages.isEmpty() ? null : pages.get(0);
    }

    public static int getWindowCount(BrowserContext context) {
        return context.pages().size();
    }

    public static void closePage(Page page) {
        page.close();
    }

    // Wait for new page to open (useful for handling target="_blank" links)
    public static Page waitForNewPage(BrowserContext context, Runnable action) {
        return context.waitForPage(action::run);
    }
}