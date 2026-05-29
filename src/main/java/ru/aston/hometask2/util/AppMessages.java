package ru.aston.hometask2.util;

import static ru.aston.hometask2.util.AppConstants.MAX_USER_AGE;
import static ru.aston.hometask2.util.AppConstants.MIN_USER_AGE;

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
    public static final String PROMPT_AGE = "Введите возраст (" + MIN_USER_AGE + "-" + MAX_USER_AGE + "): ";
    public static final String PROMPT_NEW_AGE = "Введите новый возраст (" + MIN_USER_AGE + "-" + MAX_USER_AGE + "): ";
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

    public static final String ERROR_USER_NULL = "Объект пользователя не может быть null!";
    public static final String ERROR_NAME_EMPTY = "Имя пользователя не может быть пустым!";
    public static final String ERROR_EMAIL_FORMAT = "Некорректный формат email адреса!";
    public static final String ERROR_ID_POSITIVE = "ID пользователя должен быть положительным числом";
    public static final String ERROR_UPDATE_ID_INVALID = "Некорректный ID пользователя для обновления";
    public static final String ERROR_DELETE_ID_INVALID = "Некорректный ID пользователя для удаления!";

    public static final String PREFIX_SUCCESS = "[УСПЕХ]: ";
    public static final String PREFIX_VALIDATION_ERROR = "[ОШИБКА ВАЛИДАЦИИ]: ";
    public static final String PREFIX_SYSTEM_ERROR = "[СИСТЕМНАЯ ОШИБКА]: ";
    public static final String PREFIX_ERROR = "[ОШИБКА]: ";

    public static final String MSG_USER_LIST_EMPTY = "Список пользователей пока пуст.";
    public static final String MSG_USER_LIST_HEADER = "=== СПИСОК ПОЛЬЗОВАТЕЛЕЙ ===";
    public static final String MSG_UPDATE_SUCCESS = "Данные пользователя обновлены!";

    public static final String ERROR_DAO_FIND_ALL = "Не удалось получить список всех пользователей";
    public static final String ERROR_DAO_SAVE = "Не удалось сохранить пользователя в БД";

    public static final String ERROR_SESSION_FACTORY_CREATION = "Initial SessionFactory creation failed: ";

    public static String getAgeRangeError(int min, int max) {
        return "Число должно быть в диапазоне от " + min + " до " + max + ".";
    }

    public static String getErrorEmailRegistered(String email) {
        return "Пользователь с email '" + email + "' уже зарегистрирован!";
    }

    public static String getErrorUserNotFound(Long id) {
        return "Пользователь с ID " + id + " не найден";
    }

    public static String getErrorEmailOccupied(String email) {
        return "Email '" + email + "' уже занят другим пользователем!";
    }

    public static String getErrorAgeRange(int min, int max) {
        return "Возраст должен быть в диапазоне от " + min + " до " + max + " лет!";
    }

    public static String getMsgRegisterSuccess(Long id) {
        return "Пользователь успешно зарегистрирован с ID: " + id;
    }

    public static String getMsgFindSuccess(Object user) {
        return "Найден пользователь: " + user;
    }

    public static String getMsgUpdateError(String details) {
        return "Не удалось обновить: " + details;
    }

    public static String getMsgDeleteSuccess(Long id) {
        return "Пользователь с ID " + id + " удален.";
    }

    public static String getErrorDaoFindById(Long id) {
        return "Ошибка поиска пользователя с ID " + id;
    }

    public static String getErrorDaoUserNotFound(Long id) {
        return "Пользователь с ID " + id + " не существует";
    }

    public static String getErrorDaoUpdate(Long id) {
        return "Пользователь с ID " + id + " не существует или не может быть обновлен!";
    }

    public static String getErrorDaoDelete(Long id) {
        return "Пользователь с ID " + id + " не существует или не может быть удален!";
    }

    public static String getMsgLoadListError(String details) {
        return "Не удалось загрузить список пользователей: " + details;
    }

    public static String getMsgDeleteError(String details) {
        return "Не удалось удалить пользователя: " + details;
    }
}
