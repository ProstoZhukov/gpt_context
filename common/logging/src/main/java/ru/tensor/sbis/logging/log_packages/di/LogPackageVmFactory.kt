package ru.tensor.sbis.logging.log_packages.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeature
import ru.tensor.sbis.logging.log_packages.domain.LogDeliveryInteractor
import ru.tensor.sbis.logging.log_packages.domain.LogPackageInteractor
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageRouter
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageViewModel
import javax.inject.Inject

/**
 * Фабрика для создания вьюмодели логов.
 *
 * @author ar.leschev
 */
internal class LogPackageVmFactory @Inject constructor(
    private val shakeDetectorObservable: Observable<Unit>,
    private val logDeliveryInteractor: LogDeliveryInteractor,
    private val logPackageInteractor: LogPackageInteractor,
    private val logPackageRouter: LogPackageRouter,
    private val appFileBrowserFeature: AppFileBrowserFeature,
) : ViewModelProvider.Factory {

    /** @SelfDocumented */
    override fun <VIEW_MODEL : ViewModel> create(modelClass: Class<VIEW_MODEL>): VIEW_MODEL {
        @Suppress("UNCHECKED_CAST")
        return LogPackageViewModel(
            shakeDetectorObservable,
            logDeliveryInteractor,
            logPackageInteractor,
            logPackageRouter,
            appFileBrowserFeature
        ) as VIEW_MODEL
    }
}