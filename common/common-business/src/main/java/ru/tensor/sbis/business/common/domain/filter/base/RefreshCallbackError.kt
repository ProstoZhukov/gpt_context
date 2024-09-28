package ru.tensor.sbis.business.common.domain.filter.base

import androidx.annotation.VisibleForTesting

/**
 * Коды ошибок контроллера
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal enum class RefreshCallbackError {
    /** Отсутствует соединение */
    NO_CONNECTION,

    /** У пользователя нет прав (пользователь не авторизовался, эксепшн на стороне контроллера) */
    NO_PERMISSION,

    /** Неизвестная ошибка */
    UNKNOWN_ERROR,

    /** Отсутствует реализация */
    NOT_IMPLEMENTED,

    /** Нет прав, это ошибка с облака, нет прав на вызов метода */
    NO_RIGHTS;

    companion object {

        @JvmStatic
        fun fromValue(value: Int): RefreshCallbackError =
            values().getOrNull(value) ?: UNKNOWN_ERROR
    }
}