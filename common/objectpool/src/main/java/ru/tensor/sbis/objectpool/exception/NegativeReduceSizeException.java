package ru.tensor.sbis.objectpool.exception;

import java.util.Locale;

/**
 * Ошибка: Неверный параметр
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class NegativeReduceSizeException extends IllegalArgumentException {

    public NegativeReduceSizeException(int reduceSize) {
        super(String.format(Locale.getDefault(), "Incorrect reduce pool parameter : %d", reduceSize));
    }

}
