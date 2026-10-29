package com.servet.theinternet.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import io.qameta.allure.Step;

/**
 * HTML5 drag and drop (draggable="true" + dragstart/drop olayları).
 * Ölçüldü (Selenium 4.51): Actions.dragAndDrop Chrome ve Edge'de çalışıyor, Firefox'ta sütunlar yer
 * DEĞİŞTİRMİYOR. JavaScript ile aynı olay sırası üç tarayıcıda da çalışıyor; testler bu yüzden onu kullanır.
 */
public class DragAndDropPage extends TheInternetPage {

    private static final By COLUMN_A = By.id("column-a");
    private static final By COLUMN_B = By.id("column-b");
    private static final By HEADERS = By.cssSelector("#columns header");

    /** Tarayıcının sürükle-bırak sırasında gönderdiği olaylar, aynı DataTransfer nesnesiyle. */
    private static final String HTML5_DRAG_AND_DROP = """
            const [source, target] = arguments;
            const data = new DataTransfer();
            const fire = (type, element) => element.dispatchEvent(
                    new DragEvent(type, { bubbles: true, cancelable: true, dataTransfer: data }));
            fire('dragstart', source);
            fire('dragenter', target);
            fire('dragover', target);
            fire('drop', target);
            fire('dragend', source);
            """;

    public DragAndDropPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open drag and drop page")
    public DragAndDropPage open() {
        openPath("drag_and_drop");
        visible(COLUMN_A);
        return this;
    }

    @Step("Drag A onto B with Selenium Actions")
    public DragAndDropPage dragAToBWithActions() {
        new Actions(driver).dragAndDrop(visible(COLUMN_A), visible(COLUMN_B)).perform();
        return this;
    }

    @Step("Drag A onto B with HTML5 drag events (JavaScript)")
    public DragAndDropPage dragAToBWithJavaScript() {
        ((JavascriptExecutor) driver).executeScript(HTML5_DRAG_AND_DROP, visible(COLUMN_A), visible(COLUMN_B));
        return this;
    }

    /** @return sütun başlıkları soldan sağa, ör. [A, B] veya yer değiştirdiyse [B, A] */
    public List<String> columnOrder() {
        return driver.findElements(HEADERS).stream().map(WebElement::getText).toList();
    }
}
