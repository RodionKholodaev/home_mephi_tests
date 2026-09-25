package io.github.rodionkholodaev.homemephi.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.rodionkholodaev.homemephi.core.Config;
import io.github.rodionkholodaev.homemephi.pages.LoginPage;
import io.github.rodionkholodaev.homemephi.pages.ProfilePage;
import io.github.rodionkholodaev.homemephi.pages.StartPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

// Epic > Feature > Story - разделы, по которым тесты сгруппированы в отчёте Allure (вкладка Behaviors)
@Epic("Личный кабинет home.mephi.ru")
@Feature("Авторизация")
class AuthTest extends BaseTest {

    @Test
    @Story("Вход в кабинет")
    @DisplayName("Кнопка «Войти» на главной ведёт на auth.mephi.ru с возвратом в кабинет")
    void loginButtonRedirectsToAuthServer() {
        LoginPage loginPage = StartPage.open(driver).clickLogin();

        assertAll(
                () -> assertTrue(loginPage.isOnAuthServer(), "Должны оказаться на странице входа auth.mephi.ru"),
                () -> assertEquals(Config.HOME_URL, loginPage.getServiceParam(), "После входа должно вернуть в кабинет")
        );
    }

    @Test
    @Story("Вход в кабинет")
    @DisplayName("Вход с верными логином и паролем открывает профиль пользователя")
    void loginWithValidCredentials() {
        // Если за TIMEOUT не откроется профиль /users/<id>, конструктор ProfilePage упадёт с TimeoutException
        ProfilePage profile = LoginPage.open(driver).loginAs(Config.login(), Config.password());

        assertEquals(Config.login(), profile.getLogin(), "Должен открыться профиль того, кто входил");
    }

    @Test
    @Story("Ошибки входа")
    @DisplayName("Вход с несуществующим логином оставляет на странице входа")
    void loginWithUnknownUser() {
        // Логин выдуманный, а не свой с неверным паролем: иначе прогоны могут заблокировать учётку
        String unknownUser = "autotest-" + UUID.randomUUID();

        LoginPage loginPage = LoginPage.open(driver).loginExpectingFailure(unknownUser, "wrong-password");

        assertAll(
                () -> assertTrue(loginPage.isOnAuthServer(), "Должны остаться на странице входа"),
                () -> assertEquals("", loginPage.getPasswordValue(), "Пароль не должен возвращаться в форму"),
                () -> assertEquals("Неверное имя пользователя и пароль.", loginPage.getErrorText(),
                        "Должно появиться сообщение об ошибке входа")
        );
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "/notifications", "/talks", "/users" })
    @Story("Защита закрытых страниц")
    @DisplayName("Закрытая страница без входа отправляет на auth.mephi.ru")
    void protectedPageRedirectsToLogin(String path) {
        driver.get(Config.BASE_URL + path);
        // Конструктор LoginPage сам дождётся формы входа
        LoginPage loginPage = new LoginPage(driver);

        assertAll(
                () -> assertTrue(loginPage.isOnAuthServer(), "Должны оказаться на странице входа auth.mephi.ru"),
                () -> assertEquals(Config.BASE_URL + path, loginPage.getServiceParam(),
                        "После входа должно вернуть на запрошенную страницу")
        );
    }

    @Test
    @Story("Ошибки входа")
    @DisplayName("Пустую форму браузер не отправляет")
    void emptyFormIsNotSubmitted() {
        LoginPage loginPage = LoginPage.open(driver).submitEmpty();

        assertAll(
                () -> assertTrue(loginPage.isOnAuthServer(), "Должны остаться на странице входа"),
                () -> assertFalse(loginPage.getUsernameValidationMessage().isEmpty(),
                        "Браузер должен показать подсказку о незаполненном логине")
        );
    }
}
