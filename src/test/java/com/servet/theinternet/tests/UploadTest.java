package com.servet.theinternet.tests;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import com.servet.framework.base.BaseWebTest;
import com.servet.theinternet.pages.UploadPage;

@Epic("The Internet")
@Feature("File upload")
public class UploadTest extends BaseWebTest {

    @Test(groups = "the-internet", description = "Test sırasında oluşturulan dosya yüklenir")
    public void uploadsFile() throws IOException {
        // Dosya test sırasında oluşur: repoda bir test dosyasına ve sabit bir yola bağımlılık yok
        Path file = Files.writeString(Files.createTempFile("upload-", ".txt"), "H11 upload test");
        try {
            UploadPage page = new UploadPage(driver()).open().upload(file);

            assertEquals(page.header(), "File Uploaded!");
            assertEquals(page.uploadedFileName(), file.getFileName().toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
