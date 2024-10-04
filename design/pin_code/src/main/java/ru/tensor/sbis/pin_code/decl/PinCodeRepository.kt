package ru.tensor.sbis.pin_code.decl

/**
 * Репозиторий компонента ввода пин-кода.
 * @param RESULT тип успешного результата выполнения проверки введенного пин-кода.
 *
 * @author mb.kruglova
 */
interface PinCodeRepository<RESULT> {

    /**
     * Получить тип действия после ввода пин-кода пользователем.
     */
    fun getConfirmationFlowAction(digits: String): ConfirmationFlowAction = ConfirmationFlowAction.SEND_CODE

    /**
     * Была нажата кнопка подтверждения или достигнута максимальная длинна ввода кода.
     * @param digits введенные пользователем цифры
     * @see [ConfirmationType]
     */
    fun onCodeEntered(digits: String): RESULT

    /**
     * Необходимо ли очищать поле ввода пин-кода если возникло исключение [error] при вызове метода [onCodeEntered].
     */
    fun needCleanCode(error: Throwable): Boolean

    /**
     * Необходимо ли закрывать окно ввода для данной [error].
     * @param error Ошибка.
     */
    fun needCloseOnError(error: Throwable): Boolean = false

    /**
     * Необходимо ли закрывать окно ввода для данной [error] и отобразить её в диалоговом окне.
     * @param error Ошибка.
     */
    fun needCloseAndDisplayOnDialogOnError(error: Throwable): Boolean = false

    /**
     * Была нажата кнопка "Получить код повторно".
     */
    fun onRetry() = Unit

    /**
     * Вызывается при ошибочном повторном вводе
     */
    fun onConfirmationError() = Unit
}