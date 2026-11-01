package com.dinuka.automation.utils.ui.playwright;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopupActions {
    private Page page;
    private static final Logger logger = LoggerFactory.getLogger(PopupActions.class);

    // Constructor to initialize the Page
    public PopupActions(Page page) {
        this.page = page;
    }

    public void acceptPopup(Dialog dialog) {
        try {
            dialog.accept();
        } catch (PlaywrightException e) {
            logger.info("Error accepting dialog: " + e.getMessage());
        }
    }

    public void setupAutoAccept() {
        page.onDialog(dialog -> {
            dialog.accept();
        });
    }

    public void dismissPopup(Dialog dialog) {
        try {
            dialog.dismiss();
        } catch (PlaywrightException e) {
            logger.info("Error dismissing dialog: " + e.getMessage());
        }
    }

    public void setupAutoDismiss() {
        page.onDialog(dialog -> {
            dialog.dismiss();
        });
    }

    public String getPopupText(Dialog dialog) {
        try {
            return dialog.message();
        } catch (PlaywrightException e) {
            logger.info("Error getting dialog message: " + e.getMessage());
            return null;
        }
    }

    public void inputTextToPrompt(Dialog dialog, String text) {
        try {
            dialog.accept(text);
        } catch (PlaywrightException e) {
            logger.info("Error sending text to prompt: " + e.getMessage());
        }
    }
}
