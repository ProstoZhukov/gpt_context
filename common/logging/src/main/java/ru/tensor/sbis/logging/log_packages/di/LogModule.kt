package ru.tensor.sbis.logging.log_packages.di

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.app_file_browser.feature.AppFileBrowserFeature
import ru.tensor.sbis.app_file_browser.feature.createAppFileBrowserFeature
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.domain.LogDeliveryService
import ru.tensor.sbis.logging.domain.LogPackageService
import ru.tensor.sbis.common.util.shakedetection.ShakeDetector
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageItemViewModel
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageRouter
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageRouterImpl
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageViewHolderCallback
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageViewModel
import ru.tensor.sbis.platform.logdelivery.generated.LogCollectionProvider

/**
 * Dagger модуль для [LogComponent]
 *
 * @author av.krymov
 */
@Module
internal class LogModule {

    /**@SelfDocumented*/
    @Provides
    fun provideContext(fragment: Fragment) = fragment.requireContext()

    /** @SelfDocumented */
    @Provides
    fun provideApplication(fragment: Fragment): Application = fragment.requireActivity().application

    /**@SelfDocumented*/
    @Provides
    fun createViewModelStore(fragment: Fragment) = fragment.viewModelStore

    /**@SelfDocumented*/
    @Provides
    fun provideDataBindingViewHolderHelper(
        callback: LogPackageViewHolderCallback
    ) = DataBindingViewHolderHelper<LogPackageItemViewModel>(
        LayoutIdViewFactory(R.layout.logging_log_package_item),
        callback
    )

    /**@SelfDocumented*/
    @Provides
    fun provideLogPackageService() =
        LogPackageService()

    /**@SelfDocumented*/
    @Provides
    fun provideLogDeliveryService() = LogDeliveryService()

    /**@SelfDocumented*/
    @Provides
    internal fun provideLogCollectionProvider(): DependencyProvider<LogCollectionProvider> {
        return DependencyProvider.create {
            return@create LogCollectionProvider.instance()
        }
    }

    /**@SelfDocumented*/
    @Provides
    internal fun provideShakeDetectorObservable(
        context: Application
    ) = ShakeDetector().create(context)

    /** @SelfDocumented */
    @Provides
    internal fun provideClipboardManager(
        context: Application
    ) = ClipboardManager(context)

    /** @SelfDocumented */
    @Provides
    internal fun provideAppFileBrowserFeature(
        viewModelStoreOwner: Fragment
    ): AppFileBrowserFeature = createAppFileBrowserFeature(viewModelStoreOwner)

    /** @SelfDocumented */
    @Provides
    internal fun provideRouter(
        fragment: Fragment,
        appFileBrowserFeature: AppFileBrowserFeature
    ): LogPackageRouter = LogPackageRouterImpl(
        fragment, appFileBrowserFeature
    )

    /** @SelfDocumented */
    @Provides
    fun provideViewModel(
        fragment: Fragment,
        factory: LogPackageVmFactory
    ) = ViewModelProvider(fragment, factory)[LogPackageViewModel::class.java]
}