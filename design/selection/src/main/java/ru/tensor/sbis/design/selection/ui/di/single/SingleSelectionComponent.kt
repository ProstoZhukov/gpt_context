package ru.tensor.sbis.design.selection.ui.di.single

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.di.SelectionComponent
import ru.tensor.sbis.design.selection.ui.di.SelectionHostScope
import ru.tensor.sbis.design.selection.ui.di.common.MetaFactoryModule
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat

/**
 * @author us.bessonov
 */
@Component(
    modules = [
        SingleHostViewModelModule::class,
        MetaFactoryModule::class
    ]
)
@SelectionHostScope
internal interface SingleSelectionComponent : SelectionComponent<SingleSelectionViewModel<SelectorItemModel>> {

    override val selectionVm: SingleSelectionViewModel<SelectorItemModel>

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: Fragment,
            @BindsInstance selectionLoader: SingleSelectionLoader<SelectorItemModel>,
            @BindsInstance selectorItemHandleStrategy: SelectorItemHandleStrategy<SelectorItemModel>,
            @BindsInstance counterFormat: CounterFormat
        ): SingleSelectionComponent
    }
}