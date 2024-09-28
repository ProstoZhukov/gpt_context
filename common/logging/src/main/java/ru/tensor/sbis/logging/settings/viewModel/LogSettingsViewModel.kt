package ru.tensor.sbis.logging.settings.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LogDelayOption
import ru.tensor.sbis.logging.data.LogLevelOption
import ru.tensor.sbis.logging.data.LogStorageIntervalOption
import ru.tensor.sbis.logging.data.LoggingConfigLocal
import ru.tensor.sbis.logging.log_packages.domain.LogDeliveryInteractor
import ru.tensor.sbis.logging.settings.model.CategoryVm
import ru.tensor.sbis.logging.settings.model.Delay
import ru.tensor.sbis.logging.settings.model.DiagnosticSettings
import ru.tensor.sbis.logging.settings.model.Level
import ru.tensor.sbis.logging.settings.model.LogQueryPlanVm
import ru.tensor.sbis.logging.settings.model.OptionVm
import ru.tensor.sbis.logging.settings.model.StorageInterval
import ru.tensor.sbis.logging.settings.model.WifiUploadVm
import timber.log.Timber

/**
 * Вьюмодель экрана настроек логирования.
 *
 * @param parentCategory текущая категория.
 * @param logDeliveryInteractor
 *
 * @author av.krymov
 */
class LogSettingsViewModel(
    private val parentCategory: BehaviorSubject<List<CategoryVm>>,
    private val logDeliveryInteractor: LogDeliveryInteractor
) : ViewModel() {

    /** @SelfDocumented **/
    private val config = MutableLiveData<LoggingConfigLocal>()

    /** @SelfDocumented **/
    val items = config.map {
        val stack = parentCategory.value!!
        return@map if (stack.isNotEmpty()) {
            stack.last().toChildVm(it)
        } else {
            mapToRootVm(it)
        }
    }

    /** @SelfDocumented **/
    val successUpdateOption = SingleLiveEvent<Nothing>()

    /** @SelfDocumented **/
    val toastMsg = SingleLiveEvent<String>()

    private val clearedDisposable = CompositeDisposable()

    private val successUpdateConsumer = Consumer<LoggingConfigLocal> { config ->
        this.config.value = config
        rollback()
        successUpdateOption.call()
    }

    private val errorUpdateConsumer = Consumer<Throwable> {
        Timber.e(it)
        toastMsg.value = it.localizedMessage
    }

    init {
        addCloseable {
            clearedDisposable.clear()
        }
    }

    private fun rollback() {
        parentCategory.onNext(parentCategory.value!!.dropLast(1))
    }

    /** @SelfDocumented **/
    fun onStart() {
        clearedDisposable += logDeliveryInteractor.getConfig()
            .subscribe(
                {
                    config.value = it
                }, {
                    Timber.e(it)
                    toastMsg.value = it.localizedMessage
                }
            )

        clearedDisposable += parentCategory.flatMap { logDeliveryInteractor.getConfig().toObservable() }
            .subscribe(
                { config.value = it },
                {
                    Timber.e(it)
                    toastMsg.value = it.localizedMessage
                }
            )
    }

    /**
     * Выбрана опция из списка.
     */
    fun onOptionClick(optionVm: OptionVm) {
        when (optionVm) {
            is OptionVm.Level -> changeLevel(optionVm.option)
            is OptionVm.Delay -> changeDelay(optionVm.option)
            is OptionVm.StorageInterval -> changeStorageInterval(optionVm.option)
            is OptionVm.DiagnosticSettings -> changeDiagnosticSettings(optionVm.option)
        }
    }

    /**
     * Выбрана категория из списка.
     */
    fun onCategoryClick(categoryVm: CategoryVm) {
        parentCategory.onNext(ArrayList(parentCategory.value!!).apply { add(categoryVm) })
    }

    /**
     * Изменить режим загрузки логов.
     *
     * @param isWifiUpload, если флаг установлен, то отправка будет производится только по Wi-Fi
     */
    fun onChangeWifiUpload(isWifiUpload: Boolean) {
        if (config.value?.logUploadWifiOnlyOption != isWifiUpload) {
            clearedDisposable += logDeliveryInteractor.updateConfig(
                isWifiUpload = isWifiUpload
            ).subscribe(
                successUpdateConsumer,
                errorUpdateConsumer
            )
        }
    }

    fun onChangeLogQueryPlan(enabled: Boolean) {
        if (config.value?.logQueryPlan != enabled) {
            clearedDisposable += logDeliveryInteractor.updateConfig(
                logQueryPlan = enabled
            ).subscribe(
                successUpdateConsumer,
                errorUpdateConsumer
            )
        }
    }


    fun onBackPressed(): Boolean {
        if (parentCategory.value!!.isEmpty()) return false
        rollback()
        return true
    }

    private fun changeLevel(level: LogLevelOption) {
        if (config.value?.logLevelOption != level) {
            clearedDisposable += logDeliveryInteractor.updateConfig(
                level = level
            ).subscribe(
                successUpdateConsumer,
                errorUpdateConsumer
            )
        }
    }

    private fun changeDelay(delay: LogDelayOption) {
        if (config.value?.logDelayOption != delay) {
            clearedDisposable += logDeliveryInteractor.updateConfig(
                delay = delay
            ).subscribe(
                successUpdateConsumer,
                errorUpdateConsumer
            )
        }
    }

    private fun changeStorageInterval(storageInterval: LogStorageIntervalOption) {
        if (config.value?.logStorageIntervalOption != storageInterval) {
            clearedDisposable += logDeliveryInteractor.updateConfig(
                storageInterval = storageInterval
            ).subscribe(
                successUpdateConsumer,
                errorUpdateConsumer
            )
        }
    }

    private fun changeDiagnosticSettings(diagnosticSettingsOption: DiagnosticSettingsOption) {
        if (config.value?.diagnosticOption != diagnosticSettingsOption) {
            clearedDisposable += logDeliveryInteractor.updateConfig(
                diagnosticSettingsOption = diagnosticSettingsOption
            ).subscribe(
                successUpdateConsumer,
                errorUpdateConsumer
            )
        }
    }

    private fun mapToRootVm(loggingConfigLocal: LoggingConfigLocal): List<Any> {
        return loggingConfigLocal.run {
            listOf(
                WifiUploadVm(loggingConfigLocal.logUploadWifiOnlyOption),
                Level(logLevelOption),
                Delay(logDelayOption),
                StorageInterval(logStorageIntervalOption),
                LogQueryPlanVm(logQueryPlan),
                DiagnosticSettings(diagnosticOption)
            )
        }
    }
}