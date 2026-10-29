package com.servet.theinternet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.qameta.allure.Step;

/** Butona basınca checkbox kaybolur / input etkinleşir; ikisi de asenkron, bir yükleme çubuğundan sonra. */
public class DynamicControlsPage extends TheInternetPage {

    private static final By CHECKBOX = By.cssSelector("#checkbox-example input[type='checkbox']");
    private static final By CHECKBOX_BUTTON = By.cssSelector("#checkbox-example button");
    private static final By INPUT = By.cssSelector("#input-example input[type='text']");
    private static final By INPUT_BUTTON = By.cssSelector("#input-example button");
    private static final By MESSAGE = By.id("message");

    public DynamicControlsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open dynamic controls page")
    public DynamicControlsPage open() {
        openPath("dynamic_controls");
        visible(CHECKBOX);
        return this;
    }

    @Step("Remove the checkbox")
    public DynamicControlsPage removeCheckbox() {
        click(CHECKBOX_BUTTON);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(CHECKBOX));
        return this;
    }

    @Step("Enable the text input and type '{text}'")
    public DynamicControlsPage enableInputAndType(String text) {
        click(INPUT_BUTTON);
        // elementToBeClickable = görünür VE etkin; disabled bir input'a yazmak hata verir
        wait.until(ExpectedConditions.elementToBeClickable(INPUT)).sendKeys(text);
        return this;
    }

    public boolean isCheckboxPresent() {
        return !driver.findElements(CHECKBOX).isEmpty();
    }

    public String inputValue() {
        return driver.findElement(INPUT).getDomProperty("value");
    }

    public String message() {
        return text(MESSAGE);
    }
}
