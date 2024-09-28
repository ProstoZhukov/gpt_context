package ru.tensor.sbis.design.checkbox.models

/**
 * Модель состояния валидации.
 *
 * @author ra.geraskin
 */
sealed class SbisCheckboxValidationState(open val text: String?) {

    /**
     * Без ошибки (обычный режим).
     */
    data class Default(override val text: String? = null) : SbisCheckboxValidationState(null)

    /**
     * С ошибкой
     * @param text - красный комментарий.
     */
    data class Error(override val text: String?) : SbisCheckboxValidationState(text)

}