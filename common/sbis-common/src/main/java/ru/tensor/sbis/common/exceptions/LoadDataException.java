package ru.tensor.sbis.common.exceptions;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Исключение в процессе загрузки данных.
 *
 * @author am.boldinov
 */
public class LoadDataException extends IllegalStateException {

    /**
     * Тип исключения.
     */
    public enum Type {
        DEFAULT,
        NO_INTERNET_CONNECTION,
        INCORRECT_LOADING_PARAMS,
        NOT_LOADED_YET,
        NOT_FOUND,
        NO_RIGHTS
    }

    /**
     * Тип исключения.
     */
    @NonNull
    private final Type mType;

    /**
     * Сообщение ошибки.
     */
    @Nullable
    private final String mErrorMessage;

    public LoadDataException(@NonNull Exception e) {
        this(Type.DEFAULT, e.getMessage());
    }

    public LoadDataException(@NonNull Type type) {
        this(type, null);
    }

    public LoadDataException(@NonNull String errorMessage) {
        this(Type.DEFAULT, errorMessage);
    }

    public LoadDataException(@NonNull Type type, @Nullable String errorMessage) {
        mType = type;
        mErrorMessage = errorMessage;
    }

    @NonNull
    public Type getType() {
        return mType;
    }

    @Nullable
    public String getErrorMessage() {
        return mErrorMessage;
    }

    public boolean errorMessageIsEmpty() {
        return TextUtils.isEmpty(mErrorMessage);
    }
}
