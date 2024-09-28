package ru.tensor.sbis.logging.log_packages.presentation

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeature
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.logging.data.DiagnosticSettingsOption
import ru.tensor.sbis.logging.data.LoggingConfigLocal
import ru.tensor.sbis.logging.log_packages.domain.LogDeliveryInteractor
import ru.tensor.sbis.logging.log_packages.domain.LogPackageInteractor
import timber.log.Timber

/**
 * Вьюмодель экрана отображения списка пакетов логов.
 */
class LogPackageViewModel(
    shakeDetectorObservable: Observable<Unit>,
    private val logDeliveryInteractor: LogDeliveryInteractor,
    private val logPackageInteractor: LogPackageInteractor,
    private val logPackageRouter: LogPackageRouter,
    private val appFileBrowserFeature: AppFileBrowserFeature,
) : ViewModel() {

    private val clearedDisposable = CompositeDisposable()
    private val filesSelectedLiveData = appFileBrowserFeature.selectedFiles.map {
        it.isNotEmpty()
    }
    val logEnabled = MutableLiveData<Boolean>()
    val toastMsg = SingleLiveEvent<String>()
    val sendLogBtnVisibility = combineVisibilities(
        logEnabled,
        logPackageRouter.fileBrowserOpenedLiveData,
        filesSelectedLiveData
    ) { logEnabled, fileBrowserOpened, filesSelected ->
        if ((fileBrowserOpened != true && logEnabled == true) || (fileBrowserOpened == true && filesSelected == true))
            VISIBLE
        else
            GONE
    }

    init {
        getLogEnabledStatus()
        logEnabled.observeForever { isLogEnabled ->
            clearedDisposable += logDeliveryInteractor
                .isLogEnabled()
                .filter { isLogEnabled != it }
                .flatMapCompletable { logDeliveryInteractor.setLogEnabled(isLogEnabled) }
                .subscribe({}, { Timber.i(it) })
        }
        clearedDisposable += shakeDetectorObservable.subscribe( {
            Timber.d("Shake event detected")
            symbolDiagnose()
        }, {
            Timber.i(it)
        })
        addCloseable { clearedDisposable.clear() }
    }

    /**
     * Инициировать отправку логов, в зависимости от выбранного конфига.
     */
    fun onSendLog() {
        clearedDisposable += logDeliveryInteractor.getConfig()
            .subscribe(
                {
                    sendLog(it)
                }, {
                    Timber.e(it)
                    toastMsg.value = it.localizedMessage
                }
            )
    }

    private fun sendLog(config: LoggingConfigLocal) {
        when (config.diagnosticOption) {
            DiagnosticSettingsOption.LOGS_ONLY -> sendLogWithoutConfirmation()
            DiagnosticSettingsOption.FULL -> sendLogWithConfirmation()
            DiagnosticSettingsOption.SELECTIVE -> selectFilesOrSendLogs()
        }
    }

    /**
     * Отправка логов без подтверждения, необходимо вызывать при подтверждении отправки логов.
     * Закрывает окно выбора файлов для отправки, если оно открыто
     */
    fun sendLogWithoutConfirmation() {
        val files = appFileBrowserFeature.selectedFiles.value
        if (logPackageRouter.fileBrowserOpenedLiveData.getBooleanOrFalse()) {
            logPackageRouter.backPressed()
            appFileBrowserFeature.reset()
        }

        val dataFiles = if (!files.isNullOrEmpty())
            files.toList()
        else
            emptyList()

        clearedDisposable += logPackageInteractor.sendLog(dataFiles)
            .subscribe({}, {
                Timber.e(it)
                toastMsg.value = it.localizedMessage
            })
    }

    private fun sendLogWithConfirmation() {
        logPackageRouter.showConfirmationDialog(appFileBrowserFeature.getSelectedTotalSize())
    }

    private fun selectFilesOrSendLogs() {
        val files = appFileBrowserFeature.selectedFiles.value
        if (logPackageRouter.fileBrowserOpenedLiveData.getBooleanOrFalse() && !files.isNullOrEmpty()) {
            sendLogWithConfirmation()
        } else {
            logPackageRouter.showAppFileBrowser()
        }
    }

    private fun getLogEnabledStatus() {
        clearedDisposable += logDeliveryInteractor.isLogEnabled()
            .subscribe(
                {
                    logEnabled.value = it
                },
                {
                    Timber.e(it)
                    toastMsg.value = it.localizedMessage
                }
            )
    }

    private fun symbolDiagnose() {
        clearedDisposable += logPackageInteractor.symbolDiagnose()
            .subscribe({}, {
                Timber.e(it)
                toastMsg.value = it.localizedMessage
            })
    }

    private fun combineVisibilities(
        first: LiveData<Boolean>,
        second: LiveData<Boolean>,
        third: LiveData<Boolean>,
        combiner: (Boolean?, Boolean?, Boolean?) -> Int?,
    ): LiveData<Int> = MediatorLiveData<Int>().apply {
        var lastA: Boolean? = null
        var lastB: Boolean? = null
        var lastC: Boolean? = null
        addSource(first) {
            lastA = it
            value = combiner(lastA, lastB, lastC)
        }
        addSource(second) {
            lastB = it
            value = combiner(lastA, lastB, lastC)
        }
        addSource(third) {
            lastC = it
            value = combiner(lastA, lastB, lastC)
        }
    }

    private fun LiveData<Boolean>.getBooleanOrFalse() = value ?: false
}