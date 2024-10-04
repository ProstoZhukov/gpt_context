package ru.tensor.sbis.design.selection.ui.di.multi

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.di.SelectionComponent
import ru.tensor.sbis.design.selection.ui.di.SelectionHostScope
import ru.tensor.sbis.design.selection.ui.di.common.FilterFunction
import ru.tensor.sbis.design.selection.ui.di.common.FilterFunctionModule
import ru.tensor.sbis.design.selection.ui.di.common.MetaFactoryModule
import ru.tensor.sbis.design.selection.ui.list.items.MultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat

/**
 * @author ma.kolpakov
 */
@Component(
    modules = [
        MultiHostViewModelModule::class,
        MetaFactoryModule::class,
        FilterFunctionModule::class
    ]
)
@SelectionHostScope
internal interface MultiSelectionComponent : SelectionComponent<MultiSelectionViewModel<SelectorItemModel>> {

    override val selectionVm: MultiSelectionViewModel<SelectorItemModel>

    val multiSelectorCustomisation: MultiSelectorCustomisation

    val filterFunction: FilterFunction

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: Fragment,
            @BindsInstance selectionLoader: MultiSelectionLoader<SelectorItemModel>,
            @BindsInstance selectorItemHandleStrategy: SelectorItemHandleStrategy<SelectorItemModel>,
            @BindsInstance counterFormat: CounterFormat
        ): MultiSelectionComponent
    }
}