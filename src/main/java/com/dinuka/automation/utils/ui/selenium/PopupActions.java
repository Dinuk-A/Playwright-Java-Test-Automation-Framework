package com.dinuka.automation.utils.ui.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopupActions {
    private static final Logger logger = LoggerFactory.getLogger(PopupActions.class);

    private PopupActions() {
        throw new UnsupportedOperationException("Utility class");
    }

    @SuppressWarnings("unused")
    private WebDriver driver;

    // Constructor to initialize the WebDriver
    public PopupActions(WebDriver driver) {
        this.driver = driver;
    }

    public void acceptPopup(Alert alert) {
        try {
            alert.accept();
        } catch (NoAlertPresentException e) {
            logger.info("No alert to accept.");
        }
    }

    public void dismissPopup(Alert alert) {
        try {
            alert.dismiss();
        } catch (NoAlertPresentException e) {
            logger.info("No alert, confirm, or prompt present to dismiss.");
        }
    }

    public String getPopupText(Alert alert) {
        try {
            return alert.getText();
        } catch (NoAlertPresentException e) {
            return null;
        }
    }

    public void inputTextToPrompt(Alert alert, String text) {
        try {
            alert.sendKeys(text);
        } catch (NoAlertPresentException e) {
            logger.info("No prompt present to send text to.");
        }
    }

}
