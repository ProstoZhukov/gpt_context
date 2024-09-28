package ru.tensor.sbis.red_button.ui.host.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.red_button.repository.RedButtonRepository
import ru.tensor.sbis.red_button.ui.host.HostFragment
import ru.tensor.sbis.red_button.ui.host.HostViewModel
import javax.inject.Inject

/**
 * Модуль для [HostFragment]
 *
 * @author ra.stepanov
 */
@Module
internal class HostModule {

    /**@SelfDocumented */
    @Provides
    fun provideVMFactory(
        resourceProvider: ResourceProvider,
        repository: RedButtonRepository,
        networkUtils: NetworkUtils,
    ): HostViewModelFactory = HostViewModelFactory(resourceProvider, repository, networkUtils)

    /**@SelfDocumented */
    @Provides
    fun provideViewModel(fragment: HostFragment, factory: HostViewModelFactory) =
        ViewModelProviders.of(fragment, factory).get(HostViewModel::class.java)
}

/**
 * Фабрика для создания вью модели фрагмента [HostFragment]
 * */
internal class HostViewModelFactory @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val repository: RedButtonRepository,
    private val networkUtils: NetworkUtils
) : ViewModelProvider.Factory {

    /**@SelfDocumented */
    @Suppress("UNCHECKED_CAST")
    override fun <VIEW_MODEL : ViewModel> create(modelClass: Class<VIEW_MODEL>): VIEW_MODEL {
        return HostViewModel(resourceProvider, repository, networkUtils) as VIEW_MODEL
    }

}