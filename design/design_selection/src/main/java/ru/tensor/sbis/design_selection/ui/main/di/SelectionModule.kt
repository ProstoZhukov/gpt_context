package ru.tensor.sbis.design_selection.ui.main.di

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.listener.SelectedItemClickListener
import ru.tensor.sbis.design_selection.contract.controller.SelectionControllerWrapper
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter.SelectedItemsAdapter
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter.SelectedItemsAdapterFactory
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.listener.SelectedItemClickDelegateImpl
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.domain.completion.button.DoneButtonViewModelFactory
import ru.tensor.sbis.design_selection.domain.list.SelectionModeProvider
import ru.tensor.sbis.design_selection.domain.list.SelectionPageSizeHelper
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.*
import ru.tensor.sbis.design_selection.ui.main.vm.SelectionViewModel
import ru.tensor.sbis.design_selection.ui.main.vm.SelectionViewModelFactory
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionContentDelegate
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionHostViewModel
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonDelegate
import javax.inject.Named

/**
 * DI модуль компонента выбора.
 *
 * @author vv.chekurda
 */
@Module(includes = [SelectionDependenciesModule::class])
internal class SelectionModule {

    @Provides
    fun provideFragmentActivity(fragment: Fragment): FragmentActivity =
        fragment.requireActivity()

    @Provides
    fun provideRouter(
        fragment: Fragment,
        @IdRes containerId: Int,
        @Named(ROUTER_STRATEGY_KEY) useReplaceStrategy: Boolean
    ): SelectionRouter =
        SelectionRouter(fragment.childFragmentManager, containerId, useReplaceStrategy)

    @Provides
    @SelectionScope
    fun provideListItemsCustomization(
        selectorCustomisation: SelectionCustomization<SelectionItem>
    ): SelectableItemsCustomization<SelectionItem> =
        selectorCustomisation.getListItemsCustomization()

    @Provides
    @SelectionScope
    fun provideSelectedItemsCustomization(
        selectorCustomisation: SelectionCustomization<SelectionItem>
    ): SelectedItemsCustomization<SelectionItem> =
        selectorCustomisation.getSelectedItemsCustomization()

    @Provides
    @SelectionScope
    fun provideDoneButtonViewModel(
        config: SelectionConfig,
        factory: DoneButtonViewModelFactory
    ): DoneButtonDelegate<SelectionItem> =
        factory.create(config.doneButtonMode)

    @Provides
    @SelectionScope
    fun provideSelectedItemClickListener(): SelectedItemClickListener<SelectionItem> =
        SelectedItemClickListener()

    @Provides
    @SelectionScope
    fun provideSelectedItemClickDelegate(
        contentDelegate: SelectionContentDelegate<SelectionItem>
    ): SelectedItemClickDelegate =
        SelectedItemClickDelegateImpl(contentDelegate.selectedItemsClickListener)

    @Provides
    @SelectionScope
    fun provideSelectedItemsAdapter(
        clickDelegate: SelectedItemClickDelegate,
        selectedCustomization: SelectedItemsCustomization<SelectionItem>
    ): SelectedItemsAdapter =
        SelectedItemsAdapterFactory(clickDelegate, selectedCustomization).create()

    @Provides
    @SelectionScope
    fun providePageSizeHelper(config: SelectionConfig): SelectionPageSizeHelper =
        SelectionPageSizeHelper(config.itemsLimit)

    @Provides
    @SelectionScope
    fun provideSelectionModeProvider(config: SelectionConfig): SelectionModeProvider =
        SelectionModeProvider(config.selectionMode)

    @Provides
    @SelectionScope
    fun provideViewModelFactory(
        selectedItemsClickListener: SelectedItemClickListener<SelectionItem>,
        doneButtonViewModel: DoneButtonDelegate<SelectionItem>,
        headerButtonContract: HeaderButtonContract<SelectionItem, FragmentActivity>?,
        rulesHelper: SelectionRulesHelper
    ): SelectionViewModelFactory =
        SelectionViewModelFactory(
            selectedItemsClickListener,
            doneButtonViewModel,
            headerButtonContract,
            rulesHelper
        )

    @Provides
    @SelectionScope
    @Suppress("UNCHECKED_CAST")
    fun provideViewModel(
        fragment: Fragment,
        factory: SelectionViewModelFactory
    ): SelectionViewModel<SelectionItem> =
        ViewModelProvider(fragment, factory)[SelectionViewModelImpl::class.java]
            as SelectionViewModel<SelectionItem>

    @Provides
    @SelectionScope
    fun provideSelectionHostViewModel(
        viewModel: SelectionViewModel<SelectionItem>
    ): SelectionHostViewModel<SelectionItem> = viewModel

    @Provides
    @SelectionScope
    fun provideSelectionContentDelegate(
        viewModel: SelectionViewModel<SelectionItem>
    ): SelectionContentDelegate<SelectionItem> = viewModel

    @Provides
    @SelectionScope
    fun provideSelectionRulesHelper(
        config: SelectionConfig,
        @Named(AUTO_HIDE_KEYBOARD_KEY) autoHideKeyboard: Boolean
    ): SelectionRulesHelper =
        SelectionRulesHelper(config, autoHideKeyboard)
}

internal typealias SelectionControllerProvider =
    SelectionControllerWrapper.Provider<Any, Any, Any, Any, Any, Any, SelectionItem>
internal typealias SelectionController = SelectionControllerWrapper<Any, Any, Any, Any, Any, Any, SelectionItem>