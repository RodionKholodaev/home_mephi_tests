package io.github.rodionkholodaev.homemephi.pages;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.github.rodionkholodaev.homemephi.core.Config;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;

// Страница входа auth.mephi.ru/login (CAS). service - куда вернуть пользователя после входа
public class LoginPage extends BasePage {
    // в query параметрах указан адрес, куда нужно перенаправить пользователя
    // https://home.mephi.ru/home
    // мы его кодируем в https%3A%2F%2Fhome.mephi.ru%2Fhome
    // поскольку / и : нельзя передавать в query параметрах напрямую
    // final означает, что значение переменной не может быть изменено после инициализации
    private static final String URL = Config.AUTH_URL + "/login?service="
            + URLEncoder.encode(Config.HOME_URL, StandardCharsets.UTF_8);

    private final By form = By.id("login-form");
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By submitButton = By.name("commit");
    // Скрытое поле, в котором сервер хранит адрес возврата
    private final By serviceInput = By.id("service");
    // Плашка "Неверное имя пользователя и пароль." над формой
    private final By errorMessage = By.cssSelector(".login .alert-danger");

    public LoginPage(WebDriver driver) {
        // обращяемся к конструктору родителя
        super(driver);
        // ждем, пока прогрузится форма входа, иначе тесты будут падать
        visible(usernameField);
    }

    // Открываем напрямую, минуя StartPage: тесты входа не должны зависеть от кнопки на главной
    @Step("Открыть страницу входа auth.mephi.ru")
    public static LoginPage open(@Param(mode = Parameter.Mode.HIDDEN) WebDriver driver) {
        driver.get(URL);
        return new LoginPage(driver);
    }

    // Allure пишет в отчёт все аргументы шага. MASKED заменяет пароль звёздочками
    @Step("Войти под пользователем {username}")
    public ProfilePage loginAs(String username, @Param(mode = Parameter.Mode.MASKED) String password) {
        fillAndSubmit(username, password);
        return new ProfilePage(driver);
    }

    // После неудачного входа сервер заново отдаёт страницу входа.
    // Ждём, пока старая форма исчезнет из DOM, иначе конструктор найдёт поле
    // на ещё не перезагруженной странице и проверки пойдут по старой странице
    @Step("Попытаться войти под пользователем {username}, ожидая ошибку")
    public LoginPage loginExpectingFailure(String username, @Param(mode = Parameter.Mode.MASKED) String password) {
        WebElement oldForm = visible(form);
        fillAndSubmit(username, password);
        wait.until(ExpectedConditions.stalenessOf(oldForm));
        return new LoginPage(driver);
    }

    // Нажимаем "Войти" без заполнения полей. Браузер не отправит форму из-за required
    @Step("Нажать «Войти» с пустыми полями")
    public LoginPage submitEmpty() {
        click(submitButton);
        return this;
    }

    public String getServiceParam() {
        return driver.findElement(serviceInput).getDomProperty("value");
    }

    public boolean isOnAuthServer() {
        return driver.getCurrentUrl().startsWith(Config.AUTH_URL + "/login");
    }

    public String getErrorText() {
        return textOf(errorMessage);
    }

    // Текст встроенной подсказки браузера ("Заполните это поле"). Пустая строка, если поле валидно
    public String getUsernameValidationMessage() {
        return visible(usernameField).getDomProperty("validationMessage");
    }

    public String getPasswordValue() {
        return visible(passwordField).getDomProperty("value");
    }

    private void fillAndSubmit(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(submitButton);
    }
}
