package com.servet.theinternet.tests;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import com.servet.framework.base.BaseWebTest;
import com.servet.theinternet.pages.NestedFramesPage;
import com.servet.theinternet.pages.WindowsPage;

@Epic("The Internet")
@Feature("Frames and windows")
public class FramesAndWindowsTest extends BaseWebTest {

    @Test(groups = "the-internet", description = "İç içe frame'lerdeki metinler okunur")
    public void readsTextFromNestedFrames() {
        NestedFramesPage page = new NestedFramesPage(driver()).open();

        assertEquals(page.middleFrameText(), "MIDDLE");
        assertEquals(page.bottomFrameText(), "BOTTOM"); // önceki adım ana belgeye döndüyse bulunur
    }

    @Test(groups = "the-internet", description = "Yeni pencereye geçilir, kapatılır, asıl pencereye dönülür")
    public void newWindowIsReadAndClosed() {
        WindowsPage page = new WindowsPage(driver()).open();

        assertEquals(page.readNewWindowAndClose(), "New Window | New Window");
        assertEquals(page.windowCount(), 1);
        assertEquals(page.title(), "The Internet");
    }
}
