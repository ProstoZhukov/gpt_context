package ru.tensor.sbis.objectpool.exception;

import java.util.Locale;

/**
 * Ошибка: Попытка запросить отрицательный размер пула
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class NegativeRequestedPoolSizeException extends IllegalArgumentException {

    public NegativeRequestedPoolSizeException(int requestSize) {
        super(String.format(Locale.getDefault(), "Attemp to request negative pool size : %d", requestSize));
    }

}
