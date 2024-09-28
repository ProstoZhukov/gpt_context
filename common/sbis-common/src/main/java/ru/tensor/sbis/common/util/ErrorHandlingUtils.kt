/**
 * Инструменты, связанные с обработкой ошибок.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.common.util

import ru.tensor.sbis.common.util.AppConfig.isDebug
import timber.log.Timber

/**
 * Выполнить потенциально опасный [action] без падений в release версии.
 * В релизной сборке шлёт предупреждение вместо исключения, в дебажной не препятствует падению.
 */
fun <T> runSafely(failureMessage: String, action: () -> T): T? = try {
    action()
} catch (e: Exception) {
    Timber.w(e, failureMessage)
    if (isDebug()) throw e
    null
}