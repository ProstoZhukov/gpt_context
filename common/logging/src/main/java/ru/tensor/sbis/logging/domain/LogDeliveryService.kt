package ru.tensor.sbis.logging.domain

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LogDelayOption
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.data.LogStorageIntervalOption
import ru.tensor.sbis.logging.data.LoggingConfigLocal
import ru.tensor.sbis.platform.logdelivery.generated.LogDeliveryController
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Контракт сервиса по отправке логов.
 *
 */
class LogDeliveryService : Feature {

    private val logDeliveryController by lazy {
        LogDeliveryController.instance()
    }

    /**
     * Получить текущий конфиг.
     */
    fun getLoggingConfigSingle(): Single<LoggingConfigLocal> {
        return Single.fromCallable {
            getLoggingConfig()
        }
    }

    /**
     * Записать новый конфиг.
     *
     * @param config
     */
    fun setLoggingConfig(config: LoggingConfigLocal): Completable {
        return Completable.fromAction {
            if (config.logLevelOption == LogLevelOption.DISABLED)
                logDeliveryController.disableLog()
            else
                logDeliveryController.enableLog()
            logDeliveryController.setConfig(LoggingConfigMapper.toController(config))
        }
    }


    /**
     * Проверить включено ли логирование.
     */
    fun isLogEnabled(): Single<Boolean> {
        return Single.fromCallable {
            logDeliveryController.isLogEnabled()
        }
    }

    /**
     * Включить логирование.
     */
    fun enableLog(): Completable {
        return Completable.fromAction {
            logDeliveryController.enableLog()
        }
    }

    /**
     * Отключит логирование.
     */
    fun disableLogCompletable(): Completable {
        return Completable.fromAction {
            disableLog()
        }
    }

    /**
     * Отключит логирование.
     */
    fun disableLog(): Completable {
        return Completable.fromAction {
            logDeliveryController.disableLog()
        }
    }

    /**
     * Обновить текущий конфиг.
     * При передаче null считается, что параметр не изменился.
     *
     * @param logLevelOption
     * @param delayOption
     * @param storageIntervalOption
     * @param wifiUpload
     *
     * @author av.krymov
     */
    fun updateLoggingConfigSingle(
        logLevelOption: LogLevelOption? = null,
        delayOption: LogDelayOption? = null,
        storageIntervalOption: LogStorageIntervalOption? = null,
        wifiUpload: Boolean? = null,
        logQueryPlan: Boolean? = null,
        diagnosticSettingsOption: DiagnosticSettingsOption? = null
    ): Single<LoggingConfigLocal> {
        return Single.fromCallable {
            updateLoggingConfig(
                logLevelOption,
                delayOption,
                storageIntervalOption,
                wifiUpload,
                logQueryPlan,
                diagnosticSettingsOption
            )
        }
    }

    /**
     * Получить текущий конфиг.
     */
    private fun getLoggingConfig(): LoggingConfigLocal =
        LoggingConfigMapper.toPresentation(logDeliveryController.getConfig())

    /**
     * Обновить текущий конфиг.
     * При передаче null считается, что параметр не изменился.
     *
     * @param logLevelOption
     * @param delayOption
     * @param storageIntervalOption
     * @param wifiUpload
     *
     * @author av.krymov
     */
    fun updateLoggingConfig(
        logLevelOption: LogLevelOption? = null,
        delayOption: LogDelayOption? = null,
        storageIntervalOption: LogStorageIntervalOption? = null,
        wifiUpload: Boolean? = null,
        logQueryPlan: Boolean? = null,
        diagnosticSettingsOption: DiagnosticSettingsOption? = null
    ): LoggingConfigLocal {
        val config = logDeliveryController.getConfig()
        logLevelOption?.also { config.logLevel = LoggingConfigMapper.unmatchLogLevel(it) }
        delayOption?.also { config.writingMode = LoggingConfigMapper.unmatchWritingMode(it) }
        storageIntervalOption?.also { config.holdPeriodDays = it.daysCount }
        wifiUpload?.also { config.uploadWifiOnly = it }
        logQueryPlan?.also { config.logQueryPlan = it }
        diagnosticSettingsOption?.also { config.diagnosticMode = it.mode }

        logDeliveryController.setConfig(config)

        return getLoggingConfig()
    }
}