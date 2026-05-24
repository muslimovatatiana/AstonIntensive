package ru.aston.hometask2.util;

public final class AppMessages {

    private AppMessages() {}

    public static final String MENU_NAME = "\n--- МЕНЮ ---";
    public static final String MENU_ITEM_CREATE_USER_DESC = "1. Создать пользователя";
    public static final String MENU_ITEM_FIND_USER_DESC = "2. Найти пользователя по ID";
    public static final String MENU_ITEM_SHOW_ALL_USERS_DESC = "3. Показать всех пользователей";
    public static final String MENU_ITEM_UPDATE_USER_DESC = "4. Обновить данные пользователя";
    public static final String MENU_ITEM_DELETE_USER_DESC = "5. Удалить пользователя";
    public static final String MENU_ITEM_EXIT_DESC = "0. Выйти из приложения";
    public static final String MENU_ACTION = "Выберите действие: ";

    public static final String PROMPT_NAME = "Введите имя: ";
    public static final String PROMPT_NEW_NAME = "Введите новое имя: ";
    public static final String PROMPT_EMAIL = "Введите email: ";
    public static final String PROMPT_NEW_EMAIL = "Введите новый email: ";
    public static final String PROMPT_AGE = "Введите возраст (0-150): ";
    public static final String PROMPT_NEW_AGE = "Введите новый возраст (0-150): ";
    public static final String PROMPT_ID = "Введите ID пользователя: ";
    public static final String PROMPT_UPDATE_ID = "Введите ID пользователя для обновления: ";
    public static final String PROMPT_DELETE_ID = "Введите ID пользователя для удаления: ";

    public static final String ERROR_EMPTY_NAME = "Поле не может быть пустым. Попробуйте еще раз.";
    public static final String ERROR_INVALID_EMAIL = "Некорректный формат email (разрешена только латиница, пример: user@mail.ru). Попробуйте еще раз.";
    public static final String ERROR_INVALID_NUMBER = "Ошибка: введите корректное целое число.";
    public static final String ERROR_INVALID_ID = "Ошибка: ID должен быть числом.";
    public static final String ERROR_INVALID_MENU = "Внимание! Неверный пункт меню!";
    public static final String ERROR_MENU = "Сбой во время работы интерактивного меню: {}";
    public static final String ERROR_START = "Критическая ошибка старта инфраструктуры: {}";
    public static final String ERROR_EXCEPTION = "Непредвиденный системный сбой приложения: {}";
    public static final String ERROR_INIT_DB = "Не удалось применить миграции Liquibase. Проверьте запуск СУБД в Docker.";
    public static final String ERROR_INIT_HYBERNATE = "Сбой инициализации Hibernate. Проверьте валидность файла hibernate.cfg.xml.";

    public static String getAgeRangeError(int min, int max) {
        return "Число должно быть в диапазоне от " + min + " до " + max + ".";
    }
}
