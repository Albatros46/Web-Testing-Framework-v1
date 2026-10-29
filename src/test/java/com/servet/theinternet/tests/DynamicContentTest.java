package com.servet.theinternet.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import com.servet.framework.base.BaseWebTest;
import com.servet.theinternet.pages.DynamicControlsPage;
import com.servet.theinternet.pages.DynamicLoadingPage;

/** Asenkron değişen sayfalar: hepsi explicit wait ile, hiçbir yerde Thread.sleep yok. */
@Epic("The Internet")
@Feature("Dynamic content")
public class DynamicContentTest extends BaseWebTest {

    @Test(groups = "the-internet", description = "Gizli element 5 sn sonra görünür olur")
    public void hiddenElementBecomesVisible() {
        DynamicLoadingPage page = new DynamicLoadingPage(driver()).open(1);
        assertTrue(page.finishIsInDom(), "Örnek 1'de element baştan DOM'da (gizli)");

        assertEquals(page.startAndWaitForText(), "Hello World!");
    }

    @Test(groups = "the-internet", description = "Element 5 sn sonra DOM'a eklenir")
    public void elementIsRenderedLater() {
        DynamicLoadingPage page = new DynamicLoadingPage(driver()).open(2);
        assertFalse(page.finishIsInDom(), "Örnek 2'de element başta DOM'da yok");

        assertEquals(page.startAndWaitForText(), "Hello World!");
    }

    @Test(groups = "the-internet", description = "Checkbox kaldırılır")
    public void checkboxIsRemoved() {
        DynamicControlsPage page = new DynamicControlsPage(driver()).open().removeCheckbox();

        assertFalse(page.isCheckboxPresent());
        assertEquals(page.message(), "It's gone!");
    }

    @Test(groups = "the-internet", description = "Devre dışı input etkinleşince yazılabilir")
    public void disabledInputIsEnabledAndTyped() {
        DynamicControlsPage page = new DynamicControlsPage(driver()).open().enableInputAndType("Servet");

        assertEquals(page.message(), "It's enabled!");
        assertEquals(page.inputValue(), "Servet");
    }
}
