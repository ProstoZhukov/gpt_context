package ru.tensor.sbis.communicator.common

import timber.log.Timber
import kotlin.system.measureTimeMillis

/**
 * Реализация хелпера для проверки времени выполнения методов контроллера при офлайн операциях.
 *
 * @author da.zhukov
 */
object ControllerHelper {

    private const val ERROR_TIME_MS = 300
    private const val WARNING_TIME_MS = 100

    /**
     * Проверка времени выполнения методов контроллера при офлайн операциях.
     */
    fun <T> checkExecutionTime(name: String, func: () -> T): T {
        val result: T
        val elapsedTimeMs = measureTimeMillis {
            result = func.invoke()
        }

        when {
            elapsedTimeMs > ERROR_TIME_MS -> Timber.e(getLogMessage(name, elapsedTimeMs))
            elapsedTimeMs > WARNING_TIME_MS -> Timber.w(getLogMessage(name, elapsedTimeMs))
        }

        return result
    }

    private fun getLogMessage(name: String, elapsedTimeMs: Long): String =
        "Метод $name выполнялся $elapsedTimeMs мс."
}