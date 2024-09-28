package ru.tensor.sbis.verification_decl.lockscreen.data

/**
 * Результат запроса биометрии.
 *
 * @author ar.leschev
 */
sealed interface BiometryResult {
    /**
     * Успешно.
     */
    object OK : BiometryResult

    /**
     * Неудачно. Исчерпано количество попыток, шторка закрыта, etc.
     *
     * @param reason причина, можно вывести если есть рекомендация от биометрии [shouldNotifyUi]
     * @param code код ошибки.
     */
    data class Failed(val reason: String, val code: Int, val shouldNotifyUi: Boolean = false) : BiometryResult
}
