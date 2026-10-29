package com.servet.theinternet.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import com.servet.framework.base.BaseWebTest;
import com.servet.theinternet.pages.BrokenImagesPage;
import com.servet.theinternet.pages.DropdownPage;
import com.servet.theinternet.pages.ShadowDomPage;

@Epic("The Internet")
@Feature("Special elements")
public class ElementsTest extends BaseWebTest {

    @Test(groups = "the-internet", description = "Shadow DOM'un içine getShadowRoot() ile girilir")
    public void readsInsideShadowDom() {
        ShadowDomPage page = new ShadowDomPage(driver()).open();

        assertFalse(page.slotIsFoundWithoutShadowRoot(), "Normal CSS araması shadow ağacına girmemeli");
        assertEquals(page.slotNameInShadowRoot(), "my-text");
        assertEquals(page.firstParagraphText(), "Let's have some different text!");
    }

    /**
     * Sayfada 3 resim var, 2'si bilerek bozuk. Test iki yoldan bakar: tarayıcı resmi çizebildi mi
     * (naturalWidth) ve sunucu resim adresine ne diyor (HTTP durum kodu)? İkisi aynı sonucu vermeli.
     */
    @Test(groups = "the-internet", description = "Kırık resimler bulunur ve HTTP ile doğrulanır")
    public void findsBrokenImages() throws IOException, InterruptedException {
        BrokenImagesPage page = new BrokenImagesPage(driver()).open();

        assertEquals(page.imageCount(), 3);
        assertEquals(page.brokenImageSources(), List.of("asdf.jpg", "hjkl.jpg"));
        assertEquals(httpStatuses(page.imageUrls()), Map.of(
                "asdf.jpg", 404,
                "hjkl.jpg", 404,
                "avatar-blank.jpg", 200));
    }

    @Test(groups = "the-internet", description = "Dropdown'dan seçim yapılır")
    public void selectsFromDropdown() {
        DropdownPage page = new DropdownPage(driver()).open();
        assertEquals(page.selected(), "Please select an option");

        assertEquals(page.select("Option 2").selected(), "Option 2");
    }

    private static Map<String, Integer> httpStatuses(List<String> urls) throws IOException, InterruptedException {
        try (HttpClient http = HttpClient.newHttpClient()) {
            Map<String, Integer> statuses = new HashMap<>();
            for (String url : urls) {
                HttpResponse<Void> response = http.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                        HttpResponse.BodyHandlers.discarding());
                statuses.put(url.substring(url.lastIndexOf('/') + 1), response.statusCode());
            }
            return statuses;
        }
    }
}
