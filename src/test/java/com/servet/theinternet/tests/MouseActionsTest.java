package com.servet.theinternet.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.List;

import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import com.servet.framework.base.BaseWebTest;
import com.servet.theinternet.pages.ContextMenuPage;
import com.servet.theinternet.pages.DragAndDropPage;
import com.servet.theinternet.pages.HoversPage;

@Epic("The Internet")
@Feature("Mouse actions")
public class MouseActionsTest extends BaseWebTest {

    @Test(groups = "the-internet", description = "Hover ile gizli açıklama görünür")
    public void hoverShowsCaption() {
        HoversPage page = new HoversPage(driver()).open();
        assertFalse(page.isCaptionVisibleWithoutHover(2));

        assertEquals(page.hoverAndReadCaption(2), "name: user2\nView profile");
    }

    @Test(groups = "the-internet", description = "Sağ tık bir alert açar")
    public void rightClickOpensAlert() {
        assertEquals(new ContextMenuPage(driver()).open().rightClickAndReadAlert(), "You selected a context menu");
    }

    @Test(groups = "the-internet", description = "HTML5 drag and drop: A ile B yer değiştirir")
    public void dragAndDropSwapsColumns() {
        DragAndDropPage page = new DragAndDropPage(driver()).open();
        assertEquals(page.columnOrder(), List.of("A", "B"));

        assertEquals(page.dragAToBWithJavaScript().columnOrder(), List.of("B", "A"));
    }
}
