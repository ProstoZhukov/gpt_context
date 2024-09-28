package ru.tensor.sbis.network_native.error;

import com.google.gson.JsonElement;

/**
 * Интерфейс описывает ошибки, характерные для СБИС-а.
 */
public interface ISbisError {

    /**
     * Неопределенная ошибка
     */
    int ERROR_UNDEFINED = -1;

    /**
     * Отсутствие интернета
     */
    int ERROR_NO_INTERNET = 1;

    /**
     * Превышено время ожидания ответа
     */
    int ERROR_CONNECTION_LIMIT = 2;

    /**
     * Не удолось определить адрес
     */
    int ERROR_HOST_UNRESOLVED = 3;

    /**
     * Возможны несколько вариантов
     */
    int ERROR_CONNECTION_TIMEOUT_CANCEL = 4;

    /**
     * Внутренняя ошибка сервера
     */
    int ERROR_INTERNAL_SERVER_ERROR = 500;

    /**
     * Не авторизованы
     */
    int ERROR_UNAUTHORIZED = 401;

    /**
     * Отсутствие прав на выполнение данной операции
     */
    int ERROR_NOT_ALLOWED = 403;

    /**
     * Метод не найден
     */
    int ERROR_NOT_FOUND = 404;

    /**
     * Сервис временно недоступен
     */
    int ERROR_SERVICE_UNAVAILABLE = 503;

    /**
     * Data is corrupted
     */
    int ERROR_CORRUPTED_DATA = 5;

    /**
     * Текст ошибки.
     */
    String getErrorMessage();

    /**
     * Текст ошибки, который можно уже показать пользователю.
     */
    String getErrorUserMessage();

    /**
     * Код ошибки.
     */
    @SuppressWarnings("unused")
    int getErrorCode();

    /**
     * JsonElement
     *
     * @return [JsonElement]
     */
    @SuppressWarnings("unused")
    JsonElement getErrorData();

}
