package ru.tensor.sbis.design.selection.ui.di.single

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.Lazy
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.bl.contract.listener.OpenHierarchyListener
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.list.listener.SingleSelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.useCaseValue
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * @author us.bessonov
 */
@Module
internal class SingleViewHolderHelperModule {

    @Provides
    @SelectionListScreenScope
    fun provideFragmentActivity(fragment: Fragment): FragmentActivity = fragment.requireActivity()

    @Provides
    @SelectionListScreenScope
    fun provideClickDelegate(
        selectionViewModel: SingleSelectionViewModel<SelectorItemModel>,
        searchViewModel: SearchViewModel,
        hierarchyListenerProvider: Lazy<OpenHierarchyListener<SelectorItemModel>?>,
        listeners: SelectorItemListeners<SelectorItemModel, FragmentActivity>,
        activityProvider: Provider<FragmentActivity>,
        arguments: Bundle
    ): SelectionClickDelegate<SelectorItemModel> =
        SingleSelectionClickDelegate(
            selectionViewModel,
            searchViewModel,
            hierarchyListenerProvider,
            listeners,
            activityProvider,
            arguments.useCaseValue
        )

    @Provides
    @SelectionListScreenScope
    fun provideViewHolderHelpers(
        clickDelegate: SelectionClickDelegate<SelectorItemModel>,
        selectorCustomisation: SelectorCustomisation,
        iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
        activityProvider: Provider<FragmentActivity>,
    ): MutableMap<Any, ViewHolderHelper<SelectorItemModel, *>> =
        selectorCustomisation.createViewHolderHelpers(clickDelegate, iconClickListener, activityProvider).toMutableMap()
}