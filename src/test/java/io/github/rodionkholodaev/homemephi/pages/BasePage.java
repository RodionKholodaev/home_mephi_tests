package io.github.rodionkholodaev.homemephi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.rodionkholodaev.homemephi.core.Config;

// Общее для всех страниц: драйвер, ожидание и действия, которые всегда ждут элемент
public abstract class BasePage {
    // дрейвер и ожидание final, чтобы наследники не могли их переопределить
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    // конструктор protected, чтобы наследники могли его вызвать, а извне нельзя было создать объект
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.TIMEOUT);
    }
    // методы обертки (чтобы не вызывать через driver)
    // ждем когда прогрузиться DOM и возвращаем его
    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    // ждем когда элемент станет кликабельным и кликаем по нему
    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }
    // ждем видимости поля, очищаем его и вводим текст
    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        field.sendKeys(text);
    }
    // ждем видимости элемента и возвращаем его текст без пробелов в начале и конце
    protected String textOf(By locator) {
        return visible(locator).getText().trim();
    }

    // Для проверок "элемента нет": не ждёт TIMEOUT, отвечает сразу
    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

}
