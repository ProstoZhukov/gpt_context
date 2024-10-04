package ru.tensor.sbis.design.selection.ui.di.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.utils.searchQueryMinLength
import ru.tensor.sbis.design.selection.ui.utils.useCaseValue
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModelFactory
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModelImpl
import javax.inject.Named

private const val SEARCH_VIEW_MODEL_FACTORY = "SEARCH_VIEW_MODEL_FACTORY"

/**
 * @author ma.kolpakov
 */
@Module
internal class SearchViewModelModule {

    @Provides
    @SelectionListScreenScope
    fun provideSearchViewModel(
        fragment: Fragment,
        @Named(SEARCH_VIEW_MODEL_FACTORY)
        factory: ViewModelProvider.Factory
    ): SearchViewModel =
        ViewModelProviders.of(fragment, factory)[SearchViewModelImpl::class.java]

    @Provides
    @Named(SEARCH_VIEW_MODEL_FACTORY)
    @SelectionListScreenScope
    fun provideViewModelFactory(
        arguments: Bundle
    ): ViewModelProvider.Factory =
        SearchViewModelFactory(arguments.searchQueryMinLength, arguments.useCaseValue)
}