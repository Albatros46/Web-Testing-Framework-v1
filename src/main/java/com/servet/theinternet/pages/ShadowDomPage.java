package com.servet.theinternet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.qameta.allure.Step;

/**
 * Shadow DOM: <my-paragraph> elementinin içi ayrı bir ağaç. Normal bir CSS/XPath oraya ulaşmaz;
 * önce getShadowRoot() ile o ağaca girilir (Selenium 4).
 */
public class ShadowDomPage extends TheInternetPage {

    private static final By HOSTS = By.tagName("my-paragraph");

    public ShadowDomPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open shadow DOM page")
    public ShadowDomPage open() {
        openPath("shadowdom");
        visible(HOSTS);
        return this;
    }

    /** Shadow ağacının içindeki slot'un adı ("my-text"). */
    @Step("Read the slot name inside the shadow root")
    public String slotNameInShadowRoot() {
        return shadowRoot().findElement(By.cssSelector("p slot")).getDomAttribute("name");
    }

    /** Shadow ağacı dışarıdan normal bir aramayla görünmez. */
    public boolean slotIsFoundWithoutShadowRoot() {
        return !driver.findElements(By.cssSelector("my-paragraph p slot")).isEmpty();
    }

    /** Slot'a yerleşen, sayfada görünen metin. */
    public String firstParagraphText() {
        return driver.findElements(HOSTS).get(0).getText();
    }

    private SearchContext shadowRoot() {
        WebElement host = driver.findElements(HOSTS).get(0);
        return host.getShadowRoot();
    }
}
