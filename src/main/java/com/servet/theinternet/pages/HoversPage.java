package com.servet.theinternet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import io.qameta.allure.Step;

/** Fare resmin üstüne gelince görünen açıklama: Actions.moveToElement ile. */
public class HoversPage extends TheInternetPage {

    private static final By FIGURES = By.cssSelector(".figure");

    public HoversPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open hovers page")
    public HoversPage open() {
        openPath("hovers");
        visible(FIGURES);
        return this;
    }

    /** @param position 1'den başlar */
    @Step("Hover over user {position}")
    public String hoverAndReadCaption(int position) {
        WebElement figure = driver.findElements(FIGURES).get(position - 1);
        new Actions(driver).moveToElement(figure).perform();
        return wait.until(d -> {
            WebElement caption = figure.findElement(By.cssSelector(".figcaption"));
            return caption.isDisplayed() ? caption.getText() : null;
        });
    }

    /** Açıklama baştan DOM'da ama gizli: fare gelmeden görünür olmamalı. */
    public boolean isCaptionVisibleWithoutHover(int position) {
        return driver.findElements(FIGURES).get(position - 1)
                .findElement(By.cssSelector(".figcaption")).isDisplayed();
    }
}
