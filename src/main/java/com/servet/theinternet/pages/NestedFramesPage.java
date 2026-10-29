package com.servet.theinternet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.qameta.allure.Step;

/**
 * Frame içinde frame: üstte üç frame (left / middle / right), altta bir frame.
 * Her frame ayrı bir belge; içindeki elementi bulmak için önce o frame'e geçmek gerekir.
 */
public class NestedFramesPage extends TheInternetPage {

    private static final By BODY = By.tagName("body");

    public NestedFramesPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open nested frames page")
    public NestedFramesPage open() {
        openPath("nested_frames");
        return this;
    }

    @Step("Read the text of the middle frame (inside the top frame)")
    public String middleFrameText() {
        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("frame-top"));
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("frame-middle"));
            return text(By.id("content"));
        } finally {
            driver.switchTo().defaultContent(); // sonraki adım yine ana belgeden başlasın
        }
    }

    @Step("Read the text of the bottom frame")
    public String bottomFrameText() {
        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("frame-bottom"));
            return text(BODY);
        } finally {
            driver.switchTo().defaultContent();
        }
    }
}
