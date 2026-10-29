package com.servet.theinternet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.qameta.allure.Step;

/** target=_blank ile açılan yeni pencere: Selenium kendiliğinden geçmez, pencere tanıtıcısıyla geçilir. */
public class WindowsPage extends TheInternetPage {

    private static final By CLICK_HERE = By.linkText("Click Here");
    private static final By HEADER = By.tagName("h3");

    public WindowsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open multiple windows page")
    public WindowsPage open() {
        openPath("windows");
        visible(CLICK_HERE);
        return this;
    }

    /**
     * Linke tıklar, yeni pencereye geçer, başlığını okur, pencereyi kapatıp asıl pencereye döner.
     * @return yeni penceredeki sayfanın başlığı ve h3 metni, " | " ile
     */
    @Step("Open the link in a new window, read it and close it")
    public String readNewWindowAndClose() {
        String original = driver.getWindowHandle();
        click(CLICK_HERE);
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        String newWindow = driver.getWindowHandles().stream()
                .filter(handle -> !handle.equals(original))
                .findFirst()
                .orElseThrow();
        driver.switchTo().window(newWindow);
        // Firefox pencereye, sayfa yüklenmeden geçer: başlık hemen okunursa boş gelir (ölçüldü).
        // Önce içerik, sonra boş olmayan bir başlık beklenir.
        String header = text(HEADER);
        String title = wait.until(d -> d.getTitle().isEmpty() ? null : d.getTitle());
        String content = title + " | " + header;

        driver.close();
        driver.switchTo().window(original);
        return content;
    }

    public int windowCount() {
        return driver.getWindowHandles().size();
    }

    public String title() {
        return driver.getTitle();
    }
}
