package com.servet.theinternet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import io.qameta.allure.Step;

/** Standart <select>: Selenium'un Select sınıfıyla. */
public class DropdownPage extends TheInternetPage {

    private static final By DROPDOWN = By.id("dropdown");

    public DropdownPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open dropdown page")
    public DropdownPage open() {
        openPath("dropdown");
        visible(DROPDOWN);
        return this;
    }

    @Step("Select '{option}'")
    public DropdownPage select(String option) {
        dropdown().selectByVisibleText(option);
        return this;
    }

    public String selected() {
        return dropdown().getFirstSelectedOption().getText();
    }

    private Select dropdown() {
        return new Select(visible(DROPDOWN));
    }
}
