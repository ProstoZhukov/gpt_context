package ru.tensor.sbis.logging.controller_logger

import android.util.Log
import ru.tensor.sbis.platform.generated.LogLevel
import ru.tensor.sbis.platform.generated.UiLogProvider

/**
 * Обёртка над логгером контроллера.
 *
 * @author av.krymov
 */
internal class ControllerLogDeliveryProvider {

    fun error(message: String) = UiLogProvider.error(message)

    fun warning(message: String) = UiLogProvider.warning(message)

    fun message(message: String, priority: Int) = UiLogProvider.message(message, getLogLevel(priority))

    private fun getLogLevel(priority: Int): LogLevel =
        when (priority) {
            /** логи уровня [Log.ERROR] и [Log.ASSERT] отправляем всегда */
            Log.ERROR, Log.ASSERT -> LogLevel.MINIMAL
            /** логи уровня [Log.INFO] и [Log.WARN] отправляем в стандартном режиме */
            Log.INFO, Log.WARN -> LogLevel.STANDARD
            /** логи уровня [Log.VERBOSE] отправляем в расширенном режиме */
            Log.VERBOSE -> LogLevel.EXTENDED
            /** логи уровня [Log.DEBUG] отправляем в отладочном режиме */
            Log.DEBUG -> LogLevel.DEBUG
            else -> LogLevel.DISABLED
        }
}