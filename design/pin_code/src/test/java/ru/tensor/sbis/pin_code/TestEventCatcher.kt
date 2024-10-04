package ru.tensor.sbis.pin_code

/**
 * Вспомогательный интерфейс для тестирования фичи.
 *
 * @author as.stafeev
 */
interface TestEventCatcher {
    fun onCanceled()

    fun onSuccess(any: Any)

    fun onCodeEntered(): String

    fun onRetry()

    fun onNeedCleanCode(): Boolean
}