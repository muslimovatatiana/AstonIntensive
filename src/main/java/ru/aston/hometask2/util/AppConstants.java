package ru.aston.hometask2.util;

public final class AppConstants {

    private AppConstants() {}

    public static final int MIN_USER_AGE = 0;
    public static final int MAX_USER_AGE = 150;

    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String MENU_ITEM_CREATE_USER_NUM = "1";
    public static final String MENU_ITEM_FIND_USER_NUM = "2";
    public static final String MENU_ITEM_SHOW_ALL_USERS_CONST_NUM = "3";
    public static final String MENU_ITEM_UPDATE_USER_CONST_NUM = "4";
    public static final String MENU_ITEM_DELETE_USER_CONST_NUM = "5";
    public static final String MENU_ITEM_EXIT_CONST_NUM = "0";

    public static final String CONNECTION_URL = "jdbc:postgresql://localhost:5433/user_service_db";
    public static final String CONNECTION_USER = "postgres";
    public static final String CONNECTION_PASSWORD = "postgres";

    public static final String RESOURCES_FILE = "src/main/resources";
    public static final String CHANGELOG_FILE = "db/changelog/db.changelog-master.xml";

    public static final String UPDATE_CMD_ARG = "database";

    public static final String DB_UNIQUE_EMAIL_CONSTRAINT = "users_email_key";
}
