package io.github.rodionkholodaev.homemephi.core;

import java.io.ByteArrayInputStream;
import java.util.function.Supplier;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Allure;

// Если тест упал, прикладывает к отчёту Allure скриншот, адрес и HTML страницы.
// AfterTestExecutionCallback, а не TestWatcher: он срабатывает до @AfterEach,
// пока браузер ещё открыт. TestWatcher вызывается после quit(), снимать было бы уже нечего
public class FailureAttachments implements AfterTestExecutionCallback {

    // Supplier, а не сам драйвер: расширение создаётся раньше, чем @BeforeEach запускает браузер
    private final Supplier<WebDriver> driver;

    public FailureAttachments(Supplier<WebDriver> driver) {
        this.driver = driver;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        WebDriver webDriver = driver.get();
        if (context.getExecutionException().isEmpty() || webDriver == null) {
            return;
        }
        // Браузер мог упасть вместе с тестом. Ошибка при снятии вложений
        // не должна заменить в отчёте настоящую причину падения
        try {
            byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Скриншот", "image/png", new ByteArrayInputStream(screenshot), "png");
            Allure.addAttachment("URL", webDriver.getCurrentUrl());
            Allure.addAttachment("HTML страницы", "text/html", webDriver.getPageSource(), "html");
        } catch (RuntimeException e) {
            Allure.addAttachment("Не удалось снять вложения", e.toString());
        }
    }
}
