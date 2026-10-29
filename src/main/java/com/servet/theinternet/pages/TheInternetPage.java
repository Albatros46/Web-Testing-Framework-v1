package com.servet.theinternet.pages;

import org.openqa.selenium.WebDriver;

import com.servet.framework.config.Config;
import com.servet.framework.pages.BasePage;

/**
 * the-internet.herokuapp.com sayfalarının ortak atası. Her örnek ayrı bir adreste durduğu için
 * sayfalar sadece kendi yolunu (ör. "javascript_alerts") bilir; site adresi ayardan gelir.
 */
abstract class TheInternetPage extends BasePage {

    protected TheInternetPage(WebDriver driver) {
        super(driver);
    }

    protected void openPath(String path) {
        driver.get(Config.get("theInternetUrl") + path);
    }
}
