package io.github.rodionkholodaev.homemephi.core;

import java.util.Map;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
// final - класс от которого нельзя наследоваться
// private конструктор - чтобы нельзя было создать объект класса вне класса
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        // не ждать загрузки картинок и сторонних скриптов (счётчики, онлайн-статус),
        // хватает готового HTML: нужные элементы тесты и так ждут явно через wait
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        if (Config.headless()) {
            // запуск без окна
            options.addArguments("--headless=new");
        }
        // размер окна
        options.addArguments("--window-size=1920,1080");
        // язык интерфейса браузера
        options.addArguments("--lang=ru-RU");
        // отключение уведомлений в браузере
        options.addArguments("--disable-notifications");
        // внутренние настройки chrome

        options.setExperimentalOption("prefs", Map.of(
                // установка языка интерфейса браузера (что будем у него запрашивать)
                "intl.accept_languages", "ru-RU,ru",
                // отключение сохранения паролей в браузере
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                // отключение проверки на утечку паролей в браузере
                "profile.password_manager_leak_detection", false
        ));
        // возвращаем объект драйвера, который будет управлять браузером
        return new ChromeDriver(options);
    }
}
