package ru.tensor.sbis.design.view.input.base

import androidx.annotation.AttrRes
import ru.tensor.sbis.design.view.input.R

/**
 * Статус валидации для изменения сообщения валидации и его цвета.
 *
 * @author ps.smirnyh
 */
sealed class ValidationStatus {
    /**
     * Сообщение валидации.
     */
    abstract val message: String

    /**
     * Используется чтобы ассоциировать цвета с типами и не использовать kotlin reflection.
     */
    @get:AttrRes
    internal abstract val tag: Int

    /**
     * Валидация по-умолчанию.
     * @property message сообщение по-умолчанию.
     *
     * @author ps.smirnyh
     */
    data class Default(
        override val message: String
    ) : ValidationStatus() {

        override val tag = R.attr.inputView_validationDefaultColor
    }

    /**
     * Валидация с ошибкой.
     * @property message детали ошибки.
     *
     * @author ps.smirnyh
     */
    data class Error(
        override val message: String
    ) : ValidationStatus() {

        override val tag = R.attr.inputView_validationErrorColor
    }

    /**
     * Валидация с замечаниями.
     * @property message замечания.
     *
     * @author ps.smirnyh
     */
    data class Warning(
        override val message: String
    ) : ValidationStatus() {

        override val tag = R.attr.inputView_validationWarningColor
    }

    /**
     * Валидация с успехом.
     * @property message детали успеха.
     *
     * @author ps.smirnyh
     */
    data class Success(
        override val message: String
    ) : ValidationStatus() {

        override val tag = R.attr.inputView_validationSuccessColor
    }
}