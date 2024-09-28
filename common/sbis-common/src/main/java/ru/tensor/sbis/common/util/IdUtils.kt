package ru.tensor.sbis.common.util

object IdUtils {
    /**
     * Значение id Long для преобразования из Nullable в NotNull.
     * Использовать только для id полей - т.к id не может быть < 0
     */
    const val NIL_ID_LONG = -1L

    /**
     * Значение id Int для преобразования из Nullable в NotNullable
     * Использовать только для id полей - т.к id не может быть < 0
     */
    const val NIL_ID_INT = -1
}