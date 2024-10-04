package ru.tensor.sbis.design.buttons.base.models.state

/**
 * Состояние кнопки.
 *
 * @author ma.kolpakov
 */
enum class SbisButtonState {

    /**
     * Включена.
     */
    ENABLED,

    /**
     * Выключена.
     */
    DISABLED,

    /**
     * Выполнение длительной операции.
     */
    IN_PROGRESS;

    /** Сопоставить состояние кнопки с её доступностью. */
    fun mapStateToAvailability(): Boolean? {
        return when (this) {
            ENABLED -> true
            DISABLED -> false
            IN_PROGRESS -> null
        }
    }
}