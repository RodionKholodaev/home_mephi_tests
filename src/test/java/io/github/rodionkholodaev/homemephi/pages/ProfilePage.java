package io.github.rodionkholodaev.homemephi.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.github.rodionkholodaev.homemephi.core.Config;

// Профиль пользователя home.mephi.ru/users/<id>.
// Цепочка после входа: auth.mephi.ru -> /home?ticket=... -> /users/<id>
public class ProfilePage extends BasePage {

    // id у каждого свой, поэтому в шаблоне любое число.
    // ^ и $ обязательны: urlMatches ищет совпадение в любой части URL, а не во всём URL
    private static final String URL_REGEX = "^" + Config.BASE_URL.replace(".", "\\.") + "/users/\\d+/?([?#].*)?$";

    // Фамилия в заголовке профиля, в title у неё логин: <span title="hrs008">Холодаев</span>.
    // Ищем span сразу после значка онлайн-статуса, чтобы не зацепить другие span с title
    private final By userLogin = By.cssSelector("h3 > .user-online-status + span[title]");

    public ProfilePage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlMatches(URL_REGEX));
        visible(userLogin);
    }

    // Логин владельца профиля, по нему тест проверяет, что вошли именно под своей учёткой
    public String getLogin() {
        return visible(userLogin).getDomAttribute("title");
    }
}
