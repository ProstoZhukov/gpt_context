package ru.tensor.sbis.common.exceptions

/**
 * Модель данных об ошибке для представления.
 *
 * @author am.boldinov
 */
data class ErrorData<EMPTY_VIEW_DATA>(
        val userMessage: String?,
        val emptyViewData: EMPTY_VIEW_DATA?
)