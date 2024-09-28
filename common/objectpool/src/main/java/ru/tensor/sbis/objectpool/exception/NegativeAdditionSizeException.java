package ru.tensor.sbis.objectpool.exception;

import java.util.Locale;

/**
 * Ошибка: Неверный параметр пула сложения
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class NegativeAdditionSizeException extends IllegalArgumentException {

    public NegativeAdditionSizeException(int additionSize) {
        super(String.format(Locale.getDefault(), "Incorrect addition pool parameter : %d", additionSize));
    }

}
