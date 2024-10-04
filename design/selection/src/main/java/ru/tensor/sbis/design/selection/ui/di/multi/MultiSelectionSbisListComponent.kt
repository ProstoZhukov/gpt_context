package ru.tensor.sbis.design.selection.ui.di.multi

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.di.SbisListComponent
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.di.common.EntityFactoryModule
import ru.tensor.sbis.design.selection.ui.di.common.InteractorModule
import ru.tensor.sbis.design.selection.ui.di.common.ListScreenViewModelModule
import ru.tensor.sbis.design.selection.ui.di.common.SelectorItemListenersModule
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.ServiceWrapper

/**
 * @author ma.kolpakov
 */
@SelectionListScreenScope
@Component(
    modules = [
        InteractorModule::class,
        ListScreenViewModelModule::class,
        MultiMapperDependencies::class,
        EntityFactoryModule::class,
        MultiViewHolderHelperModule::class,
        MultiSelectorListFilterMetaFactoryModule::class,
        SelectorItemListenersModule::class,
    ],
    dependencies = [MultiSelectionComponent::class]
)
internal interface MultiSelectionSbisListComponent : SbisListComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: Fragment,
            @BindsInstance serviceWrapper: ServiceWrapper<Any, Any>?,
            @BindsInstance selectionLoader: MultiSelectionLoader<SelectorItemModel>,
            @BindsInstance filterFactory: FilterFactory<SelectorItemModel, Any, Any>,
            @BindsInstance resultHelper: ResultHelper<Any, Any>,
            @BindsInstance mapper: ListMapper<Any, SelectorItemModel>,
            @BindsInstance selectorStrings: SelectorStrings,
            @BindsInstance enableRecentSelectionCaching: Boolean,
            selectionComponent: MultiSelectionComponent
        ): MultiSelectionSbisListComponent
    }
}