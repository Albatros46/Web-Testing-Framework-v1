package com.servet.theinternet.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

/**
 * Kırık resim: tarayıcı resmi yükleyemediğinde genişliği 0 kalır (naturalWidth).
 * Selenium'da "resim bozuk" diye bir komut yok; bilgiyi JavaScript ile tarayıcıdan alırız.
 */
public class BrokenImagesPage extends TheInternetPage {

    private static final By IMAGES = By.cssSelector(".example img");
    private static final String IS_BROKEN = "return arguments[0].complete && arguments[0].naturalWidth === 0;";

    public BrokenImagesPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open broken images page")
    public BrokenImagesPage open() {
        openPath("broken_images");
        // Resimlerin hepsi yüklenmeyi BİTİRMİŞ olmalı (başarıyla ya da hatayla), yoksa sonuç rastgele olur
        wait.until(d -> d.findElements(IMAGES).stream()
                .allMatch(img -> (Boolean) ((JavascriptExecutor) d).executeScript("return arguments[0].complete;", img)));
        return this;
    }

    /** @return kırık resimlerin src değerleri, sayfadaki sırayla */
    @Step("Find broken images")
    public List<String> brokenImageSources() {
        return driver.findElements(IMAGES).stream()
                .filter(img -> (Boolean) ((JavascriptExecutor) driver).executeScript(IS_BROKEN, img))
                .map(img -> img.getDomAttribute("src"))
                .toList();
    }

    /** @return tüm resimlerin tam adresleri (HTTP ile ayrıca kontrol etmek için) */
    public List<String> imageUrls() {
        return driver.findElements(IMAGES).stream().map(img -> img.getDomProperty("src")).toList();
    }

    public int imageCount() {
        return driver.findElements(IMAGES).size();
    }
}
