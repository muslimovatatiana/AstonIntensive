package ru.aston.hometask2;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.hometask2.controllers.UserController;
import ru.aston.hometask2.dao.impl.UserDaoImpl;
import ru.aston.hometask2.services.impl.UserServiceImpl;
import ru.aston.hometask2.util.HibernateUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ru.aston.hometask2.util.AppConstants.CHANGELOG_FILE;
import static ru.aston.hometask2.util.AppConstants.CONNECTION_PASSWORD;
import static ru.aston.hometask2.util.AppConstants.CONNECTION_URL;
import static ru.aston.hometask2.util.AppConstants.CONNECTION_USER;
import static ru.aston.hometask2.util.AppConstants.EMAIL_REGEX;
import static ru.aston.hometask2.util.AppConstants.MAX_USER_AGE;
import static ru.aston.hometask2.util.AppConstants.MENU_ITEM_CREATE_USER_NUM;
import static ru.aston.hometask2.util.AppConstants.MENU_ITEM_DELETE_USER_CONST_NUM;
import static ru.aston.hometask2.util.AppConstants.MENU_ITEM_EXIT_CONST_NUM;
import static ru.aston.hometask2.util.AppConstants.MENU_ITEM_FIND_USER_NUM;
import static ru.aston.hometask2.util.AppConstants.MENU_ITEM_SHOW_ALL_USERS_CONST_NUM;
import static ru.aston.hometask2.util.AppConstants.MENU_ITEM_UPDATE_USER_CONST_NUM;
import static ru.aston.hometask2.util.AppConstants.MIN_USER_AGE;
import static ru.aston.hometask2.util.AppConstants.RESOURCES_FILE;
import static ru.aston.hometask2.util.AppConstants.UPDATE_CMD_ARG;
import static ru.aston.hometask2.util.AppMessages.ERROR_EMPTY_NAME;
import static ru.aston.hometask2.util.AppMessages.ERROR_EXCEPTION;
import static ru.aston.hometask2.util.AppMessages.ERROR_INIT_DB;
import static ru.aston.hometask2.util.AppMessages.ERROR_INIT_HYBERNATE;
import static ru.aston.hometask2.util.AppMessages.ERROR_INVALID_EMAIL;
import static ru.aston.hometask2.util.AppMessages.ERROR_INVALID_ID;
import static ru.aston.hometask2.util.AppMessages.ERROR_INVALID_MENU;
import static ru.aston.hometask2.util.AppMessages.ERROR_INVALID_NUMBER;
import static ru.aston.hometask2.util.AppMessages.ERROR_MENU;
import static ru.aston.hometask2.util.AppMessages.ERROR_START;
import static ru.aston.hometask2.util.AppMessages.MENU_ACTION;
import static ru.aston.hometask2.util.AppMessages.MENU_ITEM_CREATE_USER_DESC;
import static ru.aston.hometask2.util.AppMessages.MENU_ITEM_DELETE_USER_DESC;
import static ru.aston.hometask2.util.AppMessages.MENU_ITEM_EXIT_DESC;
import static ru.aston.hometask2.util.AppMessages.MENU_ITEM_FIND_USER_DESC;
import static ru.aston.hometask2.util.AppMessages.MENU_ITEM_SHOW_ALL_USERS_DESC;
import static ru.aston.hometask2.util.AppMessages.MENU_ITEM_UPDATE_USER_DESC;
import static ru.aston.hometask2.util.AppMessages.MENU_NAME;
import static ru.aston.hometask2.util.AppMessages.PROMPT_AGE;
import static ru.aston.hometask2.util.AppMessages.PROMPT_DELETE_ID;
import static ru.aston.hometask2.util.AppMessages.PROMPT_EMAIL;
import static ru.aston.hometask2.util.AppMessages.PROMPT_ID;
import static ru.aston.hometask2.util.AppMessages.PROMPT_NAME;
import static ru.aston.hometask2.util.AppMessages.PROMPT_NEW_AGE;
import static ru.aston.hometask2.util.AppMessages.PROMPT_NEW_EMAIL;
import static ru.aston.hometask2.util.AppMessages.PROMPT_NEW_NAME;
import static ru.aston.hometask2.util.AppMessages.PROMPT_UPDATE_ID;
import static ru.aston.hometask2.util.AppMessages.getAgeRangeError;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            initDataBase();
            initSession();
            initMenu(initServices());
        } catch (IllegalStateException e) {
            log.error(ERROR_START, e.getMessage(), e);
        } catch (Exception e) {
            log.error(ERROR_EXCEPTION, e.getMessage(), e);
        }
    }

    private static void initDataBase() {
        try (Connection connection = DriverManager.getConnection(
                CONNECTION_URL, CONNECTION_USER, CONNECTION_PASSWORD)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            File resourcesDir = new File(RESOURCES_FILE);
            DirectoryResourceAccessor resourceAccessor = new DirectoryResourceAccessor(resourcesDir);

            CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, CHANGELOG_FILE);
            updateCommand.addArgumentValue(UPDATE_CMD_ARG, database);

            Map<String, Object> scopeObjects = new HashMap<>();
            scopeObjects.put(Scope.Attr.resourceAccessor.name(), resourceAccessor);

            Scope.child(scopeObjects, updateCommand::execute);
        } catch (Exception e) {
            throw new IllegalStateException(ERROR_INIT_DB, e);
        }
    }

    private static UserController initServices() {
        UserDaoImpl userDao = new UserDaoImpl();
        UserServiceImpl userService = new UserServiceImpl(userDao);
        return new UserController(userService);
    }

    private static void initSession() {
        try {
            if (HibernateUtil.getSessionFactory() == null) {
                throw new IllegalStateException();
            }
        } catch (Exception e) {
            throw new IllegalStateException(ERROR_INIT_HYBERNATE, e);
        }
    }

    private static void initMenu(UserController userController) {
        try (Scanner in = new Scanner(System.in, System.lineSeparator().equals("\r\n") ? "UTF-8" : Charset.defaultCharset().displayName())) {
            boolean running = true;

            while (running) {
                printMenu();
                String choice = in.nextLine().trim();

                switch (choice) {
                    case MENU_ITEM_CREATE_USER_NUM -> {
                        String name = readValidString(in, PROMPT_NAME);
                        String email = readValidEmail(in, PROMPT_EMAIL);
                        int age = readValidInt(in, PROMPT_AGE, MIN_USER_AGE, MAX_USER_AGE);
                        userController.createUser(name, email, age);
                    }
                    case MENU_ITEM_FIND_USER_NUM -> {
                        Long id = readValidLong(in, PROMPT_ID);
                        userController.showUserById(id);
                    }
                    case MENU_ITEM_SHOW_ALL_USERS_CONST_NUM -> userController.showAllUsers();
                    case MENU_ITEM_UPDATE_USER_CONST_NUM -> {
                        Long id = readValidLong(in, PROMPT_UPDATE_ID);
                        String name = readValidString(in, PROMPT_NEW_NAME);
                        String email = readValidEmail(in, PROMPT_NEW_EMAIL);
                        int age = readValidInt(in, PROMPT_NEW_AGE, MIN_USER_AGE, MAX_USER_AGE);
                        userController.updateUser(id, name, email, age);
                    }
                    case MENU_ITEM_DELETE_USER_CONST_NUM -> {
                        Long id = readValidLong(in, PROMPT_DELETE_ID);
                        userController.deleteUser(id);
                    }
                    case MENU_ITEM_EXIT_CONST_NUM -> running = false;
                    default -> System.out.println(ERROR_INVALID_MENU);
                }
            }
        } catch (Exception e) {
            log.error(ERROR_MENU, e.getMessage(), e);
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void printMenu() {
        System.out.println(MENU_NAME);
        System.out.println(MENU_ITEM_CREATE_USER_DESC);
        System.out.println(MENU_ITEM_FIND_USER_DESC);
        System.out.println(MENU_ITEM_SHOW_ALL_USERS_DESC);
        System.out.println(MENU_ITEM_UPDATE_USER_DESC);
        System.out.println(MENU_ITEM_DELETE_USER_DESC);
        System.out.println(MENU_ITEM_EXIT_DESC);
        System.out.print(MENU_ACTION);
    }

    private static String readValidString(Scanner in, String prompt) {
        String input = "";
        while (isStringCorrect(input)) {
            System.out.print(prompt);
            input = in.nextLine().trim();
            if (isStringCorrect(input)) {
                System.out.println(ERROR_EMPTY_NAME);
            }
        }
        return input;
    }

    private static boolean isStringCorrect(String input) {
        return input.isEmpty();
    }

    private static String readValidEmail(Scanner in, String prompt) {
        String email = "";
        while (isEmailCorrect(email)) {
            System.out.print(prompt);
            email = in.nextLine().trim();
            if (isEmailCorrect(email)) {
                System.out.println(ERROR_INVALID_EMAIL);
            }
        }
        return email;
    }

    private static boolean isEmailCorrect(String email) {
        return email.isEmpty() || !email.matches(EMAIL_REGEX);
    }

    private static int readValidInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim();

            if (isNotInteger(input)) {
                System.out.println(ERROR_INVALID_NUMBER);
                continue;
            }

            int value = Integer.parseInt(input);
            if (isAgeOutOfRange(value, min, max)) {
                System.out.println(getAgeRangeError(min, max));
                continue;
            }

            return value;
        }
    }

    private static boolean isNotInteger(String input) {
        try {
            Integer.parseInt(input);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private static boolean isAgeOutOfRange(int value, int min, int max) {
        return value < min || value > max;
    }

    private static Long readValidLong(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = in.nextLine().trim();

            if (isNotLong(input)) {
                System.out.println(ERROR_INVALID_ID);
                continue;
            }

            return Long.parseLong(input);
        }
    }

    private static boolean isNotLong(String input) {
        try {
            Long.parseLong(input);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
