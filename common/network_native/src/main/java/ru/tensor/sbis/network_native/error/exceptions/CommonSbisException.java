package ru.tensor.sbis.network_native.error.exceptions;

import androidx.annotation.NonNull;

import ru.tensor.sbis.network_native.error.ISbisError;

/**
 * Базовое исключение
 */
public class CommonSbisException extends Exception {

    private final String mErrorMessage;
    private final String mErrorUserMessage;

    /**
     * Конструктор
     *
     * @param error Ошибка
     */
    CommonSbisException(@NonNull ISbisError error) {
        super(error.getErrorMessage());
        mErrorMessage = error.getErrorMessage();
        mErrorUserMessage = error.getErrorUserMessage();
    }

    /**
     * Конструктор
     *
     * @param errorMessage     Текст ошибки
     * @param errorUserMessage Текст ошибки, который можно показать пользователю.\
     */
    public CommonSbisException(String errorMessage,
                               String errorUserMessage) {
        super(errorMessage);
        this.mErrorMessage = errorMessage;
        this.mErrorUserMessage = errorUserMessage;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    @SuppressWarnings("unused")
    public String getErrorUserMessage() {
        return mErrorUserMessage;
    }

}
