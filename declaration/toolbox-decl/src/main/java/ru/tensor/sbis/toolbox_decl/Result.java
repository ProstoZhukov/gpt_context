package ru.tensor.sbis.toolbox_decl;

import androidx.annotation.NonNull;

/**
 * Модель для хранения результата операции и кода ее ошибки
 */
public class Result {

    /**
     * @param errorText описание ошибки
     * @return объект для представления неудачного результата
     */
    public static Result newInstanceForFail(@NonNull String errorText) {
        return new Result(false, errorText);
    }

    /**
     * Объект для представления успешного результата
     */
    @NonNull
    public static Result SUCCESS = new Result(true, "");
    @NonNull
    public final String errorText;
    public final boolean success;

    private Result(boolean success,
                   @NonNull String errorText) {
        this.success = success;
        this.errorText = errorText;
    }
}
