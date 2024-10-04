package ru.tensor.sbis.design.selection.ui.di.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.bl.contract.listener.OpenHierarchyListener
import ru.tensor.sbis.design.selection.ui.di.SelectionComponent
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.di.createFixedButtonViewModel
import ru.tensor.sbis.design.selection.ui.di.createOpenHierarchyListener
import ru.tensor.sbis.design.selection.ui.list.SelectionListInteractor
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntity
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenVM
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.SelectorListScreenViewModel
import ru.tensor.sbis.design.selection.ui.utils.vm.SelectorListScreenViewModelFactory
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.ChooseAllFixedButtonViewModel
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel
import ru.tensor.sbis.list.base.presentation.getViewModel
import javax.inject.Named

/**
 * Под этим именем поставляются аргументы отдельного списка (уровня вложенности)
 *
 * @see SelectionComponent.arguments
 */
internal const val LIST_ARGUMENTS = "LIST_ARGUMENTS"

/**
 * @author ma.kolpakov
 */
@Module(includes = [SearchViewModelModule::class])
internal class ListScreenViewModelModule {

    @Provides
    @SelectionListScreenScope
    @Named(LIST_ARGUMENTS)
    fun provideListArguments(fragment: Fragment): Bundle =
        fragment.requireArguments()

    @Provides
    @SelectionListScreenScope
    fun provideHierarchyListener(
        arguments: Bundle,
        listViewModel: SelectionListScreenVM
    ): OpenHierarchyListener<SelectorItemModel>? =
        createOpenHierarchyListener(arguments, listViewModel)

    @Provides
    @SelectionListScreenScope
    fun provideFixedButtonViewModel(
        arguments: Bundle
    ): FixedButtonViewModel<Any> =
        createFixedButtonViewModel(arguments)

    @Provides
    @SelectionListScreenScope
    fun provideChooseAllViewModel(
        fixedButtonViewModel: FixedButtonViewModel<Any>
    ): ChooseAllFixedButtonViewModel? =
        fixedButtonViewModel as? ChooseAllFixedButtonViewModel

    @Provides
    @SelectionListScreenScope
    fun provideListViewModel(fragment: Fragment, factory: ViewModelProvider.Factory): SelectionListScreenVM =
        ViewModelProvider(fragment, factory)[SelectorListScreenViewModel::class.java]

    @Provides
    @SelectionListScreenScope
    fun provideViewModelFactory(
        fragment: Fragment,
        entity: SelectionListScreenEntity<Any, Any, Any>,
        filterCreator: SelectorFilterCreator<Any, Any>,
        interactor: SelectionListInteractor<Any, Any, Any, SelectionListScreenEntity<Any, Any, Any>>,
        fixedButtonViewModel: FixedButtonViewModel<Any>
    ): ViewModelProvider.Factory = SelectorListScreenViewModelFactory(
        entity,
        filterCreator,
        interactor,
        fixedButtonViewModel,
        fragment.getViewModel(interactor, entity)
    )
}