package com.servet.theinternet.pages;

import java.nio.file.Path;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Step;

/**
 * Dosya yükleme: işletim sisteminin dosya penceresi otomatikleştirilemez, ama gerek de yok:
 * dosyanın yolu doğrudan input type=file elementine yazılır.
 * Selenium Grid'de dosya test makinesinde, tarayıcı başka bir makinede olur; DriverFactory bu yüzden
 * RemoteWebDriver'a LocalFileDetector verir ve dosya önce Grid düğümüne kopyalanır.
 */
public class UploadPage extends TheInternetPage {

    private static final By FILE_INPUT = By.id("file-upload");
    private static final By SUBMIT = By.id("file-submit");
    private static final By HEADER = By.tagName("h3");
    private static final By UPLOADED_FILES = By.id("uploaded-files");

    public UploadPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open file upload page")
    public UploadPage open() {
        openPath("upload");
        visible(FILE_INPUT);
        return this;
    }

    @Step("Upload {file}")
    public UploadPage upload(Path file) {
        // type() kullanılmaz: clear() dosya input'unda çalışmaz
        driver.findElement(FILE_INPUT).sendKeys(file.toAbsolutePath().toString());
        click(SUBMIT);
        visible(UPLOADED_FILES);
        return this;
    }

    public String header() {
        return text(HEADER);
    }

    public String uploadedFileName() {
        return text(UPLOADED_FILES).trim();
    }
}
