package com.servet.theinternet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

/**
 * "Start"a basınca ~5 sn yükleme, sonra "Hello World!".
 * Örnek 1: element baştan DOM'da ama gizli. Örnek 2: element 5 sn sonra DOM'a EKLENİR.
 * İkisinde de aynı explicit wait (görünür olana kadar bekle) çalışır; findElement hemen çağrılsaydı
 * örnek 1'de gizli elementin boş metni, örnek 2'de NoSuchElementException gelirdi.
 */
public class DynamicLoadingPage extends TheInternetPage {

    private static final By START = By.cssSelector("#start button");
    private static final By FINISH = By.cssSelector("#finish h4");

    public DynamicLoadingPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open dynamic loading example {example}")
    public DynamicLoadingPage open(int example) {
        openPath("dynamic_loading/" + example);
        visible(START);
        return this;
    }

    @Step("Start loading and wait for the result")
    public String startAndWaitForText() {
        click(START);
        return text(FINISH);
    }

    public boolean finishIsInDom() {
        return !driver.findElements(FINISH).isEmpty();
    }
}
