package ru.tensor.sbis.design_selection.ui.content.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModelFactory
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModelImpl

/**
 * DI модуль поиска для области контента компонента выбора.
 *
 * @author vv.chekurda
 */
@Module
internal class SelectionContentSearchModule {

    @Provides
    @SelectionContentScope
    fun provideViewModelFactory(): SelectionSearchViewModelFactory =
        SelectionSearchViewModelFactory()

    @Provides
    @SelectionContentScope
    fun provideSearchViewModel(
        fragment: Fragment,
        factory: SelectionSearchViewModelFactory
    ): SelectionSearchViewModel =
        ViewModelProvider(fragment, factory)[SelectionSearchViewModelImpl::class.java]
}