package io.github.rodionkholodaev.homemephi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.github.rodionkholodaev.homemephi.core.Config;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;

// Главная home.mephi.ru для неавторизованного пользователя: большая кнопка "Войти"
public class StartPage extends BasePage {

    private static final String URL = Config.BASE_URL + "/";

    // Кнопка внутри формы, которая ведёт на /home. По тексту не ищем: текст могут поменять
    private final By loginButton = By.cssSelector("form[action$='/home'] button[type='submit']");

    public StartPage(WebDriver driver) {
        super(driver);
        visible(loginButton);
    }

    @Step("Открыть главную home.mephi.ru")
    public static StartPage open(@Param(mode = Parameter.Mode.HIDDEN) WebDriver driver) {
        driver.get(URL);
        return new StartPage(driver);
    }

    // /home без сессии перекидывает на auth.mephi.ru, поэтому возвращаем страницу входа
    @Step("Нажать «Войти» на главной")
    public LoginPage clickLogin() {
        click(loginButton);
        return new LoginPage(driver);
    }
}
