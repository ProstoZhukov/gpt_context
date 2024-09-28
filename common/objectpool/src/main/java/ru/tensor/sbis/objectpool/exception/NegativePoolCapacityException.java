package ru.tensor.sbis.objectpool.exception;

import java.util.Locale;

/**
 * Ошибка: Попытка установить отрицательную емкость пула
 * @author am.boldinov
 */
public class NegativePoolCapacityException extends IllegalArgumentException {

    public NegativePoolCapacityException(int capacity) {
        super(String.format(Locale.getDefault(), "Attempt to set negative pool capacity : %d", capacity));
    }

}
