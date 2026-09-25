package io.github.rodionkholodaev.homemephi.core;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();

        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--lang=ru-RU");
        options.addArguments("--disable-notifications");

        options.setExperimentalOption("prefs", Map.of(
                "intl.accept_languages", "ru-RU,ru",
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false
        ));

        return new ChromeDriver(options);
    }
}
