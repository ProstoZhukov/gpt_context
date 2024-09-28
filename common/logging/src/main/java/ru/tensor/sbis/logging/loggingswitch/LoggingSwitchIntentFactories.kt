/**
 * Инструменты для Intent по управлению логгированием посредством LoggingSwitchBroadcastReceiver
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.logging.loggingswitch

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.logging.data.LogLevelOption

internal const val SWITCH_LOGGING_INTENT_ACTION = "SWITCH_LOGGING_INTENT_ACTION"
internal const val ARG_ENABLE_LOGS = "ARG_ENABLE_LOGS"
internal const val ARG_EXPORT_LOGS = "ARG_EXPORT_LOGS"
internal const val ARG_LOGS_PATH = "ARG_LOGS_PATH"
internal const val ARG_LOGS_LEVEL = "ARG_LOGS_LEVEL"
internal const val LOG_QUERY_PLAN = "LOG_QUERY_PLAN"
internal const val ADB_LOGS_LEVEL = "LOGS_LEVEL"

/**
 * Создаёт [Intent] для включения логгирования
 *
 * @see LoggingSwitchBroadcastReceiver
 */
fun createEnableLoggingBroadcastIntent(
    logsLevel: LogLevelOption = LogLevelOption.DEBUG
) = Intent(SWITCH_LOGGING_INTENT_ACTION).apply {
    putExtra(ARG_ENABLE_LOGS, true)
    putExtra(ARG_LOGS_LEVEL, logsLevel)
}

/**
 * Создаёт [Intent] для отключения логгирования.
 *
 * @param exportLogs нужно ли выгружать логи во внешнее хранилище после отключения
 * @param logsExportPath путь к подпапке для выгрузки логов вида "/subFolder1/subFolder2/destination" (корневая
 * папка определяется через [Context.getExternalFilesDir])
 *
 * @see LoggingSwitchBroadcastReceiver
 */
fun createDisableLoggingBroadcastIntent(
    exportLogs: Boolean = false,
    logsExportPath: String = ""
) = Intent(SWITCH_LOGGING_INTENT_ACTION).apply {
    putExtra(ARG_ENABLE_LOGS, false)
    putExtra(ARG_EXPORT_LOGS, exportLogs)
    putExtra(ARG_LOGS_PATH, logsExportPath)
}