package com.servet.theinternet.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.qameta.allure.Step;

/** JavaScript alert / confirm / prompt: tarayıcının kendi penceresi, DOM'da değil. */
public class AlertsPage extends TheInternetPage {

    private static final By ALERT_BUTTON = By.xpath("//button[text()='Click for JS Alert']");
    private static final By CONFIRM_BUTTON = By.xpath("//button[text()='Click for JS Confirm']");
    private static final By PROMPT_BUTTON = By.xpath("//button[text()='Click for JS Prompt']");
    private static final By RESULT = By.id("result");

    public AlertsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open JavaScript alerts page")
    public AlertsPage open() {
        openPath("javascript_alerts");
        visible(ALERT_BUTTON);
        return this;
    }

    /** @return alert penceresindeki metin */
    @Step("Accept a JS alert")
    public String acceptAlert() {
        click(ALERT_BUTTON);
        Alert alert = waitForAlert();
        String text = alert.getText();
        alert.accept();
        return text;
    }

    @Step("Dismiss a JS confirm")
    public AlertsPage dismissConfirm() {
        click(CONFIRM_BUTTON);
        waitForAlert().dismiss();
        return this;
    }

    @Step("Answer a JS prompt with '{answer}'")
    public AlertsPage answerPrompt(String answer) {
        click(PROMPT_BUTTON);
        Alert prompt = waitForAlert();
        prompt.sendKeys(answer);
        prompt.accept();
        return this;
    }

    public String result() {
        return text(RESULT);
    }

    // Alert açıkken sayfadaki hiçbir elemente dokunulamaz; önce alert'in gerçekten açılmasını bekleriz
    private Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }
}
