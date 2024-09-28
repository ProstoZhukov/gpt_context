package ru.tensor.sbis.objectpool.exception;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Ошибка: Пул уже существует
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ObjectAlreadyExistsInPoolException extends IllegalStateException {

    public ObjectAlreadyExistsInPoolException(@NonNull Object obj) {
        super(String.format(Locale.getDefault(), "%s already exists in pool (type of object: %s)",
                obj.toString(), obj.getClass().getCanonicalName()));
    }

}
