package com.servet.theinternet.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.qameta.allure.Step;

/** Sağ tık (contextmenu olayı) bir JS alert açar. */
public class ContextMenuPage extends TheInternetPage {

    private static final By HOT_SPOT = By.id("hot-spot");

    public ContextMenuPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open context menu page")
    public ContextMenuPage open() {
        openPath("context_menu");
        visible(HOT_SPOT);
        return this;
    }

    /** @return sağ tıktan sonra açılan alert'in metni */
    @Step("Right-click the box")
    public String rightClickAndReadAlert() {
        new Actions(driver).contextClick(visible(HOT_SPOT)).perform();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }
}
