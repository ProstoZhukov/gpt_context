package ru.tensor.sbis.design.selection.ui.di.single

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModelImpl
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.di.SelectionHostScope
import ru.tensor.sbis.design.selection.ui.di.createFixedButtonListener
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonListener
import ru.tensor.sbis.design.selection.ui.utils.singleCustomisation
import ru.tensor.sbis.design.selection.ui.utils.vm.SingleSelectorHostViewModelFactory

/**
 * Модуль для предоставления зависимостей уровня компонента одиночного выбора (одни на все уровни иерархии)
 *
 * @author ma.kolpakov
 */
@Module
internal class SingleHostViewModelModule {

    @Provides
    @SelectionHostScope
    fun provideArguments(fragment: Fragment): Bundle =
        fragment.requireArguments()

    @Provides
    @SelectionHostScope
    fun provideCustomisation(arguments: Bundle): SelectorCustomisation =
        arguments.singleCustomisation

    @Provides
    @SelectionHostScope
    fun provideFixedButtonListener(
        arguments: Bundle
    ): FixedButtonListener<Any, FragmentActivity>? =
        createFixedButtonListener(arguments) {
            TODO("https://online.sbis.ru/opendoc.html?guid=278e7482-68fd-4963-a53b-2eadadf29933")
        }

    @Provides
    @Suppress("UNCHECKED_CAST")
    @SelectionHostScope
    fun provideSelectionViewModel(fragment: Fragment, factory: ViewModelProvider.Factory):
        SingleSelectionViewModel<SelectorItemModel> =
        ViewModelProvider(fragment, factory)[SingleSelectionViewModelImpl::class.java]
            as SingleSelectionViewModel<SelectorItemModel>

    @Provides
    @SelectionHostScope
    fun provideViewModelFactory(
        selectionLoader: SingleSelectionLoader<SelectorItemModel>,
        metaFactory: ItemMetaFactory
    ): ViewModelProvider.Factory =
        SingleSelectorHostViewModelFactory(selectionLoader, metaFactory)
}