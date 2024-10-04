package ru.tensor.sbis.design.selection.ui.di.multi

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModelImpl
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.di.SelectionHostScope
import ru.tensor.sbis.design.selection.ui.di.createDoneButtonViewModel
import ru.tensor.sbis.design.selection.ui.di.createFixedButtonListener
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.list.items.MultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonListener
import ru.tensor.sbis.design.selection.ui.utils.multiCustomisation
import ru.tensor.sbis.design.selection.ui.utils.selectionLimit
import ru.tensor.sbis.design.selection.ui.utils.selectionMode
import ru.tensor.sbis.design.selection.ui.utils.vm.SelectorHostViewModelFactory

/**
 * Модуль для предоставления зависимостей уровня компонента множественного выбора (одни на все уровни иерархии)
 *
 * @author ma.kolpakov
 */
@Module
internal class MultiHostViewModelModule {

    @Provides
    @SelectionHostScope
    fun provideArguments(fragment: Fragment): Bundle =
        fragment.requireArguments()

    @Provides
    @SelectionHostScope
    fun provideMultiCustomisation(arguments: Bundle): MultiSelectorCustomisation =
        arguments.multiCustomisation

    @Provides
    @SelectionHostScope
    fun provideCustomisation(customisation: MultiSelectorCustomisation): SelectorCustomisation =
        customisation

    @Provides
    @SelectionHostScope
    fun provideFixedButtonListener(
        arguments: Bundle,
        selectionViewModel: MultiSelectionViewModel<SelectorItemModel>
    ): FixedButtonListener<Any, FragmentActivity>? =
        createFixedButtonListener(arguments, selectionViewModel::setSelected)

    @Provides
    @SelectionHostScope
    fun provideDoneButtonViewModel(arguments: Bundle): DoneButtonViewModel =
        createDoneButtonViewModel(arguments)

    @Provides
    @Suppress("UNCHECKED_CAST")
    @SelectionHostScope
    fun provideSelectionViewModel(fragment: Fragment, factory: ViewModelProvider.Factory):
        MultiSelectionViewModel<SelectorItemModel> =
        ViewModelProviders.of(fragment, factory)[MultiSelectionViewModelImpl::class.java]
            as MultiSelectionViewModel<SelectorItemModel>

    @Provides
    @SelectionHostScope
    fun provideViewModelFactory(
        selectionLoader: MultiSelectionLoader<SelectorItemModel>,
        metaFactory: ItemMetaFactory,
        arguments: Bundle,
        doneButtonViewModel: DoneButtonViewModel
    ): ViewModelProvider.Factory =
        SelectorHostViewModelFactory(
            selectionLoader,
            metaFactory,
            arguments.selectionLimit,
            arguments.selectionMode,
            doneButtonViewModel
        )
}