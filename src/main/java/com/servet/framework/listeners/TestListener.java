package com.servet.framework.listeners;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Allure;

/**
 * Her test olayını loglar (SLF4J → konsol + target/logs/test-run.log); test başarısız olursa ekran görüntüsü alır.
 *
 * TestNG, onTestFailure'ı @AfterMethod'dan ÖNCE ve testle AYNI thread'de çağırır.
 * Bu yüzden DriverManager.get() burada tam olarak başarısız olan testin tarayıcısını döndürür.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("START  {}", name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASS   {} ({} ms)", name(result), duration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAIL   {} -> {}", name(result), result.getThrowable().getMessage());
        Path screenshot = takeScreenshot(result);
        if (screenshot != null) {
            // Reporter.log: TestNG'nin HTML raporunda (target/surefire-reports) da görünür
            Reporter.setCurrentTestResult(result);
            Reporter.log("Screenshot: " + screenshot.toAbsolutePath());
            LOG.info("Screenshot: {}", screenshot.toAbsolutePath());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // RetryAnalyzer tekrar denemeye karar verdiyse, başarısız deneme "skipped" olarak işaretlenir
        LOG.warn("SKIP   {} ({})", name(result), result.wasRetried() ? "will be retried" : "skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("DONE   <{}>: {} passed, {} failed, {} skipped", context.getName(),
                context.getPassedTests().size(), context.getFailedTests().size(), context.getSkippedTests().size());
    }

    /**
     * Ekran görüntüsünü target/screenshots altına kaydeder ve Allure raporuna ekler.
     * Listener asla exception fırlatmamalı.
     */
    private static Path takeScreenshot(ITestResult result) {
        byte[] png = Screenshots.capture();
        if (png == null) {
            return null;
        }
        Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png), "png");
        String fileName = result.getTestClass().getRealClass().getSimpleName() + "_"
                + result.getMethod().getMethodName() + "_"
                + LocalDateTime.now().format(TIMESTAMP) + ".png";
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            return Files.write(SCREENSHOT_DIR.resolve(fileName), png);
        } catch (IOException e) {
            LOG.warn("Screenshot could not be saved: {}", e.getMessage());
            return null;
        }
    }

    private static String name(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
    }

    private static long duration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
