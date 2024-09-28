package ru.tensor.sbis.logging

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LogDelayOption
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.data.LogStorageIntervalOption
import ru.tensor.sbis.logging.data.LoggingConfigLocal
import ru.tensor.sbis.logging.domain.LogDeliveryService
import ru.tensor.sbis.toolbox_decl.logging.ForceLogDeliveryScreenProvider
import timber.log.Timber

/**
 * Класс для создания и инициализации [LoggingComponent].
 */
class LoggingComponentInitializer(private val forceLogDeliveryScreenProvider: ForceLogDeliveryScreenProvider?) :
    BaseSingletonComponentInitializer<LoggingComponent>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): LoggingComponent {
        return DaggerLoggingComponent.builder()
            .appContext(commonSingletonComponent.context)
            .forceLogDeliveryScreenProvider(forceLogDeliveryScreenProvider)
            .build().apply { enableLogDeliveryForDebug(logDeliveryService()) }
    }

    /**
     * Принудительное включение логов только для DEBUG сборки
     */
    @SuppressLint("CheckResult")
    private fun enableLogDeliveryForDebug(service: LogDeliveryService) {
        if (!BuildConfig.DEBUG) return

        service.isLogEnabled().flatMapCompletable { isLogEnabled ->
            if (isLogEnabled.not()) {
                service.enableLog().andThen(
                    service.setLoggingConfig(
                        LoggingConfigLocal(
                            logLevelOption = LogLevelOption.DEBUG,
                            logDelayOption = LogDelayOption.DELAYED,
                            logStorageIntervalOption = LogStorageIntervalOption.ONE_DAY,
                            logUploadWifiOnlyOption = true,
                            logQueryPlan = false,
                            diagnosticOption = DiagnosticSettingsOption.LOGS_ONLY
                        )
                    )
                )
            } else Completable.complete()
        }.subscribeOn(Schedulers.io()).subscribe({}, { Timber.i(it) })
    }
}