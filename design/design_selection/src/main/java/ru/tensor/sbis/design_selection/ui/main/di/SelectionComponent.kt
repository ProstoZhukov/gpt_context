package ru.tensor.sbis.design_selection.ui.main.di

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory
import ru.tensor.sbis.design_selection.domain.SelectionControllerHolder
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.customization.SelectionStrings
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener
import ru.tensor.sbis.design_selection.ui.content.di.SelectionStubFactory
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter.SelectedItemsAdapter
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.domain.list.SelectionModeProvider
import ru.tensor.sbis.design_selection.domain.list.SelectionPageSizeHelper
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionContentDelegate
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionHostViewModel
import javax.inject.Named

/**
 * DI компонент компонента выбора.
 *
 * @author vv.chekurda
 */
@Component(modules = [SelectionModule::class])
@SelectionScope
internal interface SelectionComponent {

    val selectionHostVM: SelectionHostViewModel<SelectionItem>
    val selectionContentDelegate: SelectionContentDelegate<SelectionItem>
    val selectionModeProvider: SelectionModeProvider
    val pageSizeHelper: SelectionPageSizeHelper
    val rulesHelper: SelectionRulesHelper
    val router: SelectionRouter
    val config: SelectionConfig

    val controllerHolder: SelectionControllerHolder
    val filterFactory: SelectionFilterFactory<Any, SelectionItemId>
    val selectionResultListener: SelectionResultListener<SelectionItem, FragmentActivity>
    val selectionStrings: SelectionStrings
    val headerButtonContract: HeaderButtonContract<SelectionItem, FragmentActivity>?
    val listItemsCustomization: SelectableItemsCustomization<SelectionItem>
    val selectedItemsAdapter: SelectedItemsAdapter
    val stubFactory: SelectionStubFactory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: Fragment,
            @BindsInstance @IdRes routerContainerId: Int,
            @BindsInstance @Named(ROUTER_STRATEGY_KEY) useRouterReplaceStrategy: Boolean,
            @BindsInstance @Named(AUTO_HIDE_KEYBOARD_KEY) autoHideKeyboard: Boolean,
            @BindsInstance config: SelectionConfig,
            @BindsInstance dependenciesProvider: SelectionDependenciesFactory.Provider<*, *>
        ): SelectionComponent
    }
}

internal const val ROUTER_STRATEGY_KEY = "ROUTER_STRATEGY_KEY"
internal const val AUTO_HIDE_KEYBOARD_KEY = "AUTO_HIDE_KEYBOARD_KEY"