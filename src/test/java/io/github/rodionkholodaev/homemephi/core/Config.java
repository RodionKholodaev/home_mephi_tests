package io.github.rodionkholodaev.homemephi.core;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;

public final class Config {

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    public static final String BASE_URL = "https://home.mephi.ru";
    public static final Duration TIMEOUT = Duration.ofSeconds(10);

    // Делаем приватный конструктор чтобы java не сделала дефолтный публичный
    private Config() {
    }

    // Методы, а не поля: переменная проверяется в момент обращения,
    // поэтому без логина падают только тесты, которым он нужен
    public static String login() {
        return required("MEPHI_LOGIN");
    }

    public static String password() {
        return required("MEPHI_PASSWORD");
    }

    // Приоритет: -Dheadless=true из командной строки, потом HEADLESS из .env/окружения, иначе false
    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", DOTENV.get("HEADLESS", "false")));
    }

    // Нужен чтобы падать с понятной ошибкой если не задана переменная окружения
    private static String required(String key) {
        String value = DOTENV.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Не задана переменная окружения " + key);
        }
        return value;
    }
}
