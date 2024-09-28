package ru.tensor.sbis.pin_code.decl

/**
 * Тип действий после ввода пин-кода пользователем.
 *
 * @author mb.kruglova
 */
enum class ConfirmationFlowAction {
    /** Подтвердить пин-код. */
    CONFIRM_CODE,

    /** Послать пин-код. */
    SEND_CODE,

    /** Подтвердить пин-код еще раз, если предыдущая попытка была некорректна. */
    TRY_CONFIRM_AGAIN,

    /** Создать пин-код заново. */
    CREATE_AGAIN
}