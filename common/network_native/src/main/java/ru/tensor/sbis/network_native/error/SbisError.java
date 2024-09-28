package ru.tensor.sbis.network_native.error;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

import ru.tensor.sbis.network_native.error.exceptions.CommonSbisException;


/**
 * SelfDocumented
 */
public class SbisError implements ISbisError {

    private final static String ERROR_UNDEFINED_MESSAGE = "Произошла неизвестная ошибка";

    // TODO: требуется актуализация данного транслятора
    private static final Map<Integer, String> errorTranslations = new HashMap<Integer, String>() {
        {
            put(ERROR_CONNECTION_TIMEOUT_CANCEL, "Не удалось выполнить запрос.\nПроверьте соединение с интернетом");
            put(ERROR_UNAUTHORIZED, "Необходима авторизация!");
            put(ERROR_CONNECTION_LIMIT, "Превышено время ожидания запроса");
            put(ERROR_NO_INTERNET, "Отсутствует подключение к интернету");
            put(ERROR_SERVICE_UNAVAILABLE, "Сервер временно недоступен");
            put(ERROR_NOT_FOUND, "Сервер временно недоступен");
            put(ERROR_HOST_UNRESOLVED, "Сервер временно недоступен");
            put(ERROR_INTERNAL_SERVER_ERROR, "Сервер временно недоступен");
            put(ERROR_NOT_ALLOWED, "Отсутствуют права на выполнение данной операции");
            put(ERROR_UNDEFINED, ERROR_UNDEFINED_MESSAGE);
        }
    };

    private String errorMessage;
    private String errorUserMessage;
    private int errorCode;
    private JsonElement errorData;

    public SbisError(String message, String userMessage, int code, JsonElement errorData) {
        setErrorMessage(message);
        setErrorUserMessage(userMessage);
        setErrorCode(code);
        setErrorData(errorData);
    }

    public JsonElement getErrorData() {
        return errorData;
    }

    private void setErrorData(JsonElement errorData) {
        this.errorData = errorData;
    }

    /**
     * Конструктор
     *
     * @param message     Сообщение
     * @param userMessage Сообщение для пользователя
     * @param code        Код ошибки
     */
    public SbisError(String message, String userMessage, int code) {
        setErrorMessage(message);
        setErrorUserMessage(userMessage);
        setErrorCode(code);
        setErrorData(null);
    }

    /**
     * Конструктор
     *
     * @param message Сообщение
     * @param code    Код ошибки
     */
    public SbisError(String message, int code) {
        setErrorMessage(message);
        setErrorUserMessage(code);
        setErrorCode(code);
        setErrorData(null);
    }

    /**
     * Конструктор
     *
     * @param exception Исключение
     */
    @SuppressWarnings("unused")
    public SbisError(@NonNull CommonSbisException exception) {
        setErrorMessage(exception.getErrorMessage());
        setErrorUserMessage(ERROR_UNDEFINED);
        setErrorCode(ERROR_UNDEFINED);
        setErrorData(null);
    }

    private void setErrorMessage(String message) {
        errorMessage = message;
    }

    /**
     * Сообщение
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    private void setErrorUserMessage(String message) {
        errorUserMessage = message;
    }

    private void setErrorUserMessage(int errorCode) {
        errorUserMessage = translateErrorMessage(errorCode);
    }

    /**
     * Пользовательское сообщение
     */
    public String getErrorUserMessage() {
        return errorUserMessage;
    }

    @NonNull
    private String translateErrorMessage(int errorCode) {
        return errorTranslations.containsKey(errorCode) ? errorTranslations.get(errorCode) : ERROR_UNDEFINED_MESSAGE;
    }

    /**
     * Выставить код ошибки.
     *
     * @param code код ошибки
     */
    public void setErrorCode(int code) {
        switch (code) {
            case ERROR_NO_INTERNET:
            case ERROR_CONNECTION_LIMIT:
            case ERROR_HOST_UNRESOLVED:
            case ERROR_INTERNAL_SERVER_ERROR:
            case ERROR_NOT_ALLOWED:
            case ERROR_NOT_FOUND:
            case ERROR_SERVICE_UNAVAILABLE:
            case ERROR_CONNECTION_TIMEOUT_CANCEL:
            case ERROR_UNAUTHORIZED:
            case ERROR_CORRUPTED_DATA:
                errorCode = code;
                break;
            default:
                errorCode = ERROR_UNDEFINED;
        }
    }

    /**
     * Прочитать код ошибки
     */
    public int getErrorCode() {
        return errorCode;
    }

}
