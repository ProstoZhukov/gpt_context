package ru.tensor.sbis.logging.settings.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.logging.log_packages.domain.LogDeliveryInteractor
import ru.tensor.sbis.logging.settings.model.CategoryVm
import ru.tensor.sbis.logging.settings.view.LogSettingsFragment
import ru.tensor.sbis.logging.settings.viewModel.LogSettingsViewModel
import javax.inject.Inject

/**
 * Сабкомпонент для поставки зависимостей для экрана настроек логирования.
 *
 * @author av.krymov
 */
@LoggingScope
@Subcomponent(modules = [LogSettingsModule::class])
interface LogSettingsComponent {

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun with(fragment: BaseFragment): Builder

        @BindsInstance
        fun parentCategory(parentCategory: CategoryVm?): Builder

        fun build(): LogSettingsComponent
    }

    fun inject(fragment: LogSettingsFragment)

}

@Module
internal class LogSettingsModule {

    @LoggingScope
    @Provides
    fun provideViewModel(
        fragment: BaseFragment,
        factory: LogSettingsViewModelFactory
    ): LogSettingsViewModel {
        return ViewModelProvider(fragment, factory)[LogSettingsViewModel::class.java]
    }
}

@LoggingScope
internal class LogSettingsViewModelFactory @Inject constructor(
    private val logDeliveryInteractor: LogDeliveryInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LogSettingsViewModel(
            logDeliveryInteractor = logDeliveryInteractor,
            parentCategory = BehaviorSubject.createDefault(listOf())
        ) as T
    }
}