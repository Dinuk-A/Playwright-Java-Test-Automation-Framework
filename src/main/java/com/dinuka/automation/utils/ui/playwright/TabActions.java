package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TabActions {

    private static final Logger logger = LoggerFactory.getLogger(TabActions.class);

    private Page page;
    private BrowserContext context;

    public TabActions(Page page, BrowserContext context) {
        this.page = page;
        this.context = context;
    }

    public Page openNewTab() {
        try {
            return context.newPage();
        } catch (PlaywrightException e) {
            logger.error("Error opening new tab: {}", e.getMessage(), e);
            return null;
        }
    }

    public Page openNewTabWithUrl(String url) {
        try {
            Page newPage = context.newPage();
            newPage.navigate(url);
            return newPage;
        } catch (PlaywrightException e) {
            logger.error("Error opening new tab with URL: {}", e.getMessage(), e);
            return null;
        }
    }

    public Page switchToTab(int tabIndex) {
        try {
            List<Page> pages = context.pages();
            if (tabIndex >= 0 && tabIndex < pages.size()) {
                Page targetPage = pages.get(tabIndex);
                targetPage.bringToFront();
                return targetPage;
            }
            throw new PlaywrightException("Tab index " + tabIndex + " not found");
        } catch (PlaywrightException e) {
            logger.error("Error switching to tab: {}", e.getMessage(), e);
            return null;
        }
    }

    public void switchToPage(Page targetPage) {
        try {
            targetPage.bringToFront();
        } catch (PlaywrightException e) {
            logger.error("Error switching to page: {}", e.getMessage(), e);
        }
    }

    public void closeCurrentTab() {
        try {
            page.close();
        } catch (PlaywrightException e) {
            logger.error("Error closing current tab: {}", e.getMessage(), e);
        }
    }

    public void closeTab(Page targetPage) {
        try {
            targetPage.close();
        } catch (PlaywrightException e) {
            logger.error("Error closing tab: {}", e.getMessage(), e);
        }
    }

    public void closeAllTabsExceptMain() {
        try {
            List<Page> pages = context.pages();
            for (int i = pages.size() - 1; i > 0; i--) {
                pages.get(i).close();
            }
            if (!pages.isEmpty()) {
                pages.get(0).bringToFront();
            }
        } catch (PlaywrightException e) {
            logger.error("Error closing tabs: {}", e.getMessage(), e);
        }
    }

    public Page switchToMainTab() {
        try {
            List<Page> pages = context.pages();
            if (!pages.isEmpty()) {
                Page mainPage = pages.get(0);
                mainPage.bringToFront();
                return mainPage;
            }
            return null;
        } catch (PlaywrightException e) {
            logger.error("Error switching to main tab: {}", e.getMessage(), e);
            return null;
        }
    }

    public void printCurrentTabTitle() {
        try {
            logger.info("Current tab title: {}", page.title());
        } catch (PlaywrightException e) {
            logger.error("Error getting current tab title: {}", e.getMessage(), e);
        }
    }

    public void printCurrentTabUrl() {
        try {
            logger.info("Current tab URL: {}", page.url());
        } catch (PlaywrightException e) {
            logger.error("Error getting current tab URL: {}", e.getMessage(), e);
        }
    }

    public int getTabCount() {
        try {
            return context.pages().size();
        } catch (PlaywrightException e) {
            logger.error("Error getting tab count: {}", e.getMessage(), e);
            return 0;
        }
    }

    public int getCurrentTabIndex() {
        try {
            List<Page> pages = context.pages();
            for (int i = 0; i < pages.size(); i++) {
                if (pages.get(i) == page) {
                    return i;
                }
            }
            return -1;
        } catch (PlaywrightException e) {
            logger.error("Error getting current tab index: {}", e.getMessage(), e);
            return -1;
        }
    }

    public List<String> getAllTabTitles() {
        try {
            List<Page> pages = context.pages();
            return pages.stream()
                .map(Page::title)
                .collect(java.util.stream.Collectors.toList());
        } catch (PlaywrightException e) {
            logger.error("Error getting all tab titles: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<String> getAllTabUrls() {
        try {
            List<Page> pages = context.pages();
            return pages.stream()
                .map(Page::url)
                .collect(java.util.stream.Collectors.toList());
        } catch (PlaywrightException e) {
            logger.error("Error getting all tab URLs: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public Page switchToTabByTitle(String title) {
        try {
            List<Page> pages = context.pages();
            for (Page p : pages) {
                if (p.title().equals(title)) {
                    p.bringToFront();
                    return p;
                }
            }
            throw new PlaywrightException("Tab with title '" + title + "' not found");
        } catch (PlaywrightException e) {
            logger.error("Error switching to tab by title: {}", e.getMessage(), e);
            return null;
        }
    }

    public Page switchToTabByUrl(String url) {
        try {
            List<Page> pages = context.pages();
            for (Page p : pages) {
                if (p.url().equals(url)) {
                    p.bringToFront();
                    return p;
                }
            }
            throw new PlaywrightException("Tab with URL '" + url + "' not found");
        } catch (PlaywrightException e) {
            logger.error("Error switching to tab by URL: {}", e.getMessage(), e);
            return null;
        }
    }

    public Page waitForNewTabAndReturn(Runnable action) {
        try {
            return context.waitForPage(action::run);
        } catch (PlaywrightException e) {
            logger.error("Error waiting for new tab: {}", e.getMessage(), e);
            return null;
        }
    }

    public List<Page> getAllPages() {
        try {
            return context.pages();
        } catch (PlaywrightException e) {
            logger.error("Error getting all pages: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public void printAllTabDetails() {
        try {
            List<Page> pages = context.pages();
            logger.info("Total tabs open: {}", pages.size());
            for (int i = 0; i < pages.size(); i++) {
                logger.info("Tab {} - Title: {} | URL: {}", i, pages.get(i).title(), pages.get(i).url());
            }
        } catch (PlaywrightException e) {
            logger.error("Error printing tab details: {}", e.getMessage(), e);
        }
    }
}