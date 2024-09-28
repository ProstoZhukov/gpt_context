package ru.tensor.sbis.red_button.data


/**
 * Перечень кастомных ошибок которые может вернуть контроллер.
 *
 * @author ra.stepanov
 */
sealed class RedButtonError(message: String? = null) : Throwable(message) {

    /**
     * Nonnull сообщение об ошибке
     */
    val errorMessage get() = message ?: ""

    /**
     * Ошибка сообщающая об отсутствии сети
     */
    object NoInternet : RedButtonError()

    /**
     * Класс ошибок с сообщением
     * @property message сообщение об ошибке
     */
    abstract class ErrorWithMessage(message: String) : RedButtonError(message)

    /**
     * Общая ошибка
     * @property message сообщение об ошибке
     */
    class General(message: String) : ErrorWithMessage(message)

    /**
     * Ошибка определения номера телефона пользователя
     * @property message сообщение об ошибке
     */
    class MobilePhone(message: String) : ErrorWithMessage(message)

    /**
     * Ошибка введенного пин-кода
     * @property message сообщение об ошибке
     */
    class Pin(message: String) : ErrorWithMessage(message)

    /**
     * Ошибка ввода неверного кода подтверждения из смс
     * @property message сообщение об ошибке
     */
    class ConfirmCode(message: String) : ErrorWithMessage(message)
}
