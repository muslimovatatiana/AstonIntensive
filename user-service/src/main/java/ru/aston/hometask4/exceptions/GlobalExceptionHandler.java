package ru.aston.hometask4.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_KEY_MESSAGE = "message";
    private static final String MSG_INTERNAL_SERVER_ERROR = "Internal server error. Please try again later.";
    private static final String MSG_DEFAULT_DUPLICATE_EMAIL = "Email already exists";
    private static final String CODE_EMAIL_DUPLICATE = "user.email.duplicate";
    private static final String DEFAULT_OBJECT_FIELD = "object";

    private static final String LOG_VALIDATION_FAILED = "Validation failed for request: {}";
    private static final String LOG_VALIDATION_FIELD_DEBUG = "Field '{}' failed validation: {}";
    private static final String LOG_NOT_FOUND = "Resource not found. Key: {}, Args: {}. Localized message: {}";
    private static final String LOG_INTEGRITY_VIOLATION = "Database constraint violation: {}. Sent localized response: {}";
    private static final String LOG_UNCAUGHT_EXCEPTION = "An unexpected error occurred on the server: ";

    private static final String LOG_INVALID_ACTION = "Invalid notification action. Key: {}, Args: {}. Localized message: {}";

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn(LOG_VALIDATION_FAILED, ex.getBindingResult().getTarget());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = (error instanceof FieldError fieldError) ? fieldError.getField() : DEFAULT_OBJECT_FIELD;
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.debug(LOG_VALIDATION_FIELD_DEBUG, fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(ResourceNotFoundException ex) {
        Locale currentLocale = LocaleContextHolder.getLocale();

        String localizedMessage = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                ex.getMessage(),
                currentLocale
        );

        log.warn(LOG_NOT_FOUND, ex.getMessage(), ex.getArgs(), localizedMessage);

        Map<String, String> error = Map.of(ERROR_KEY_MESSAGE, localizedMessage);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Locale currentLocale = LocaleContextHolder.getLocale();

        String localizedMessage = messageSource.getMessage(
                CODE_EMAIL_DUPLICATE,
                null,
                MSG_DEFAULT_DUPLICATE_EMAIL,
                currentLocale
        );

        log.warn(LOG_INTEGRITY_VIOLATION, ex.getMostSpecificCause().getMessage(), localizedMessage);

        Map<String, String> error = Map.of(ERROR_KEY_MESSAGE, localizedMessage);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllUncaughtExceptions(Exception ex) {
        log.error(LOG_UNCAUGHT_EXCEPTION, ex);

        Map<String, String> error = Map.of(ERROR_KEY_MESSAGE, MSG_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidNotificationActionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidNotificationActionException(InvalidNotificationActionException ex) {
        Locale currentLocale = LocaleContextHolder.getLocale();

        String localizedMessage = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                ex.getMessage(),
                currentLocale
        );

        log.warn(LOG_INVALID_ACTION, ex.getMessage(), ex.getArgs(), localizedMessage);

        Map<String, String> error = Map.of(ERROR_KEY_MESSAGE, localizedMessage);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
