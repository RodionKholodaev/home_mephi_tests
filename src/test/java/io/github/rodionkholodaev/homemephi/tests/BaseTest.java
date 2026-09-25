package io.github.rodionkholodaev.homemephi.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

import io.github.rodionkholodaev.homemephi.core.Config;
import io.github.rodionkholodaev.homemephi.core.DriverFactory;
import io.github.rodionkholodaev.homemephi.pages.LoginPage;
import io.github.rodionkholodaev.homemephi.pages.ProfilePage;

// Свежий браузер на каждый тест: чистые cookies, никакого состояния от прошлого теста.
// Для тестов входа (AuthTest). Остальные классы будут наследовать LoggedInBaseTest.
public abstract class BaseTest {

    protected WebDriver driver;

    // Имена не setUp/tearDown специально: если наследник объявит метод с таким же именем,
    // он переопределит этот, и драйвер молча перестанет создаваться
    @BeforeEach
    void startDriver() {
        driver = DriverFactory.createDriver();
    }

    // Вход под учёткой из Config. Тест вызывает его сам, только когда ему нужен вход
    protected ProfilePage loginAsDefaultUser() {
        return LoginPage.open(driver).loginAs(Config.login(), Config.password());
    }

    // Проверка на null: если createDriver() упал, JUnit всё равно вызовет @AfterEach,
    // и без проверки поверх настоящей ошибки вылетел бы NullPointerException
    @AfterEach
    void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
