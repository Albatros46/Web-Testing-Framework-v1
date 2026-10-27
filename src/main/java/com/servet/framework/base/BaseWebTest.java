package com.servet.framework.base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.servet.framework.config.Config;
import com.servet.framework.driver.DriverManager;

/**
 * Her UI testinin atası: her test kendi temiz tarayıcısıyla başlar ve tarayıcı test bitince kapanır.
 * Uygulamaya özgü hiçbir şey bilmez; bir uygulamanın BaseTest'i bunu genişletip kendi giriş
 * noktasını (ör. openLoginPage()) ekler.
 *
 * Tarayıcı bir alanda DEĞİL, DriverManager'daki ThreadLocal'da tutulur. TestNG bir sınıfın tüm
 * testleri için tek bir nesne kullandığından, alanda tutulan bir driver paralel testlerde
 * thread'ler arasında paylaşılır ve testler birbirinin tarayıcısını kullanır.
 */
public abstract class BaseWebTest {

    /** Tarayıcı önceliği: testng.xml parametresi → -Dbrowser → config-{env}.properties → config.properties */
    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void startBrowser(@Optional String browser) {
        String chosen = (browser == null || browser.isBlank()) ? Config.browser() : browser;
        DriverManager.start(chosen, Config.headless());
    }

    @AfterMethod(alwaysRun = true)
    public void quitBrowser() {
        DriverManager.quit();
    }

    protected WebDriver driver() {
        return DriverManager.get();
    }
}
