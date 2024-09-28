package ru.tensor.sbis.logging.loggingswitch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.reactivex.Completable
import io.reactivex.CompletableSource
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.log_packages.domain.LogDeliveryInteractor
import ru.tensor.sbis.logging.log_packages.domain.LogPackageInteractor
import timber.log.Timber
import java.io.File

/**
 * Служит для включения/выключения логгирования и опциональной выгрузки логов во внешнее хранилище.
 *
 * @see [createEnableLoggingBroadcastIntent]
 * @see [createDisableLoggingBroadcastIntent]
 * adb shell am broadcast -a SWITCH_LOGGING_INTENT_ACTION --ez ARG_ENABLE_LOGS true --ez ARG_EXPORT_LOGS true
 * --es LOGS_LEVEL DEBUG --es ARG_LOGS_PATH <Path to log>
 * @author us.bessonov
 */
@SuppressLint("CheckResult")
internal class LoggingSwitchBroadcastReceiver(
    private val logDeliveryInteractor: LogDeliveryInteractor,
    private val logPackageInteractor: LogPackageInteractor
) : EntryPointBroadcastReceiver() {

    override fun onReady(context: Context, intent: Intent) = with(intent) {
        val extras = extras ?: return

        if (isLoggingEnabled()) {
            enable(
                getLogLevel(extras),
                logQueryPlan = getBooleanExtra(LOG_QUERY_PLAN, false)
            )
        } else {
            disable(
                shouldExportLogs(),
                logsExportPath = getPath(context, getStringExtra(ARG_LOGS_PATH).orEmpty())
            )
        }
    }

    private fun Intent.shouldExportLogs() = getBooleanExtra(ARG_EXPORT_LOGS, false)

    private fun Intent.isLoggingEnabled() = getBooleanExtra(ARG_ENABLE_LOGS, false)

    private fun Intent.getLogLevel(extras: Bundle) = if (extras.containsKey(ADB_LOGS_LEVEL)) {
        val logLevelString = extras.getString(ADB_LOGS_LEVEL)
        LogLevelOption.valueOf(logLevelString!!)
    } else {
        getSerializableExtra(ARG_LOGS_LEVEL) as LogLevelOption
    }

    private fun enable(logsLevel: LogLevelOption, logQueryPlan: Boolean) {
        logDeliveryInteractor.apply {
            updateConfig(level = logsLevel, logQueryPlan = logQueryPlan)
                .ignoreElement()
                .concatWithAndFinishWhenComplete(setLogEnabled(true))
        }
    }

    private fun disable(exportLogs: Boolean, logsExportPath: String) {
        logDeliveryInteractor.setLogEnabled(false)
            .concatWithAndFinishWhenComplete(if (exportLogs) exportLogs(logsExportPath) else Completable.complete())
    }

    private fun Completable.concatWithAndFinishWhenComplete(source: CompletableSource) {
        val pendingResult = goAsync()
        concatWith(source)
            .subscribe(pendingResult::finish) {
                Timber.i(it)
                pendingResult.finish()
            }
    }

    private fun exportLogs(path: String): Completable {
        val directory = File(path)
        if (!directory.exists()) {
            check(directory.mkdirs()) {
                "Cannot create directory $directory"
            }
        }
        return logPackageInteractor.exportLogs(directory.absolutePath)
    }

    private fun getPath(context: Context, specificPath: String): String {
        return checkNotNull(context.getExternalFilesDir(null)?.absolutePath) {
            "Cannot get external files directory"
        } + specificPath
    }
}