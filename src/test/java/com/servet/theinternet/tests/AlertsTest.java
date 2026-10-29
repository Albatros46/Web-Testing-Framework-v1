package com.servet.theinternet.tests;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import com.servet.framework.base.BaseWebTest;
import com.servet.theinternet.pages.AlertsPage;

@Epic("The Internet")
@Feature("JavaScript alerts")
public class AlertsTest extends BaseWebTest {

    @Test(groups = "the-internet", description = "Alert metni okunur ve kapatılır")
    public void alertIsAccepted() {
        AlertsPage page = new AlertsPage(driver()).open();

        assertEquals(page.acceptAlert(), "I am a JS Alert");
        assertEquals(page.result(), "You successfully clicked an alert");
    }

    @Test(groups = "the-internet", description = "Confirm'de 'İptal' seçilir")
    public void confirmIsDismissed() {
        assertEquals(new AlertsPage(driver()).open().dismissConfirm().result(), "You clicked: Cancel");
    }

    @Test(groups = "the-internet", description = "Prompt'a metin yazılır")
    public void promptReceivesText() {
        assertEquals(new AlertsPage(driver()).open().answerPrompt("Servet").result(), "You entered: Servet");
    }
}
