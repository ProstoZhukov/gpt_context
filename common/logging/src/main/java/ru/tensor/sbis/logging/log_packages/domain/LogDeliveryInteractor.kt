package ru.tensor.sbis.logging.log_packages.domain

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LogDelayOption
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.data.LogStorageIntervalOption
import ru.tensor.sbis.logging.data.LoggingConfigLocal
import ru.tensor.sbis.logging.domain.LogDeliveryService
import javax.inject.Inject

/**
 * Реализация базового интерактора для экрана настроек логирования.
 *
 * @param logDeliveryService
 *
 * @author av.krymov
 */
open class LogDeliveryInteractor @Inject constructor(
    private val logDeliveryService: LogDeliveryService
) {
    /**
     * Получить конфиг с настройками.
     */
    fun getConfig(): Single<LoggingConfigLocal> {
        return logDeliveryService.getLoggingConfigSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Записать конфиг с настройками.
     */
    fun setConfig(config: LoggingConfigLocal): Completable {
        return logDeliveryService.setLoggingConfig(config)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Обновить конфиг с настройками.
     *
     * @param level
     * @param delay
     * @param storageInterval
     * @param isWifiUpload
     */
    fun updateConfig(
        level: LogLevelOption? = null,
        delay: LogDelayOption? = null,
        storageInterval: LogStorageIntervalOption? = null,
        isWifiUpload: Boolean? = null,
        logQueryPlan: Boolean? = null,
        diagnosticSettingsOption: DiagnosticSettingsOption? = null
    ): Single<LoggingConfigLocal> {
        return logDeliveryService.updateLoggingConfigSingle(
            logLevelOption = level,
            delayOption = delay,
            storageIntervalOption = storageInterval,
            wifiUpload = isWifiUpload,
            logQueryPlan = logQueryPlan,
            diagnosticSettingsOption = diagnosticSettingsOption
        )
    }

    /**
     * Получить состояние логирование (включено/выключено).
     */
    fun isLogEnabled(): Single<Boolean> {
        return logDeliveryService.isLogEnabled()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Изменить состояние логирования.
     *
     * @param isEnabled включить/выключить
     */
    fun setLogEnabled(isEnabled: Boolean): Completable {
        val source = if (isEnabled) logDeliveryService.enableLog() else logDeliveryService.disableLog()

        return source.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}