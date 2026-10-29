package com.servet.framework.driver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servet.framework.config.Config;

/**
 * Tarayıcıyı oluşturur: yerelde veya (gridUrl doluysa) Selenium Grid'de.
 * Yerelde driver dosyası (chromedriver.exe vb.) indirmeye gerek yok: Selenium Manager doğru sürümü bulur.
 * Grid'de aynı Options nesnesi RemoteWebDriver'a verilir; testler farkı görmez.
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    /** Tarayıcı başlatma denemesi sayısı (test değil, sadece tarayıcının açılması tekrar denenir). */
    private static final int MAX_START_ATTEMPTS = 3;

    private DriverFactory() {
    }

    /**
     * Paralel çalışmada (ölçüldü) Firefox, Chrome ve Edge aynı anda açılırken arada bir
     * "SessionNotCreated ... Process unexpectedly closed" ile başlayamıyor. Bu altyapı hatasıdır,
     * test hatası değil: sadece tarayıcıyı açmayı tekrar deneriz ve her denemeyi loglarız.
     */
    public static WebDriver create(String browser, boolean headless) {
        SessionNotCreatedException lastError = null;
        for (int attempt = 1; attempt <= MAX_START_ATTEMPTS; attempt++) {
            try {
                return newDriver(browser, headless);
            } catch (SessionNotCreatedException e) {
                lastError = e;
                LOG.warn("{} could not start (attempt {}/{}): {}", browser, attempt, MAX_START_ATTEMPTS,
                        e.getMessage().lines().findFirst().orElse(""));
            }
        }
        throw lastError;
    }

    private static WebDriver newDriver(String browser, boolean headless) {
        WebDriver driver = switch (browser.toLowerCase()) {
            case "chrome" -> start(chromeOptions(headless), headless, ChromeDriver::new);
            case "edge" -> start(edgeOptions(headless), headless, EdgeDriver::new);
            case "firefox" -> start(firefoxOptions(headless), headless, FirefoxDriver::new);
            default -> throw new IllegalArgumentException("Desteklenmeyen tarayıcı: " + browser);
        };
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Config.pageLoadTimeoutSeconds()));
        // Implicit wait BİLEREK 0: explicit wait ile karıştırılırsa bekleme süreleri öngörülemez olur.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        return driver;
    }

    /** gridUrl varsa RemoteWebDriver, yoksa yerel driver. Options ikisinde de aynı. */
    private static <O extends Capabilities> WebDriver start(O options, boolean headless,
            Function<O, WebDriver> localDriver) {
        LOG.info("Starting {} {} (headless={})", options.getBrowserName(),
                Config.gridUrl().map(url -> "on Grid " + url).orElse("locally"), headless);
        return Config.gridUrl()
                .map(url -> remote(url, options))
                .orElseGet(() -> localDriver.apply(options));
    }

    private static WebDriver remote(String gridUrl, Capabilities options) {
        try {
            RemoteWebDriver driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
            // Dosya yükleme: sendKeys(yerel yol) ile verilen dosya önce Grid düğümüne kopyalanır.
            // Bu olmadan tarayıcı dosyayı kendi (konteyner) diskinde arar ve bulamaz.
            driver.setFileDetector(new LocalFileDetector());
            return driver;
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Geçersiz gridUrl: " + gridUrl, e);
        }
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        // saucedemo'nun şifresi herkese açık olduğu için Chrome "şifren sızdırıldı" penceresi açar; kapatıyoruz.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        return options;
    }
}
