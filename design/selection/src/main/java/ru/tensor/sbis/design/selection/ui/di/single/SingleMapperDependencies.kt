package ru.tensor.sbis.design.selection.ui.di.single

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.list.RecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.contract.list.SelectorMergeFunction
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.di.common.FilterFunction
import ru.tensor.sbis.design.selection.ui.di.common.ITEMS_MERGE_FUNCTION
import ru.tensor.sbis.design.selection.ui.di.common.SELECTION_CACHING_FUNCTION
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.IdleRecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.utils.SingleSelectionFilterFunction
import ru.tensor.sbis.design.selection.ui.utils.SingleSelectionMergeFunction
import javax.inject.Named

/**
 * @author ma.kolpakov
 */
@Module
internal class SingleMapperDependencies {

    @Provides
    @SelectionListScreenScope
    fun providesFilterFunction(): FilterFunction =
        SingleSelectionFilterFunction()

    @Provides
    @SelectionListScreenScope
    @Named(ITEMS_MERGE_FUNCTION)
    fun providesMergeFunction(selectionViewModel: SingleSelectionViewModel<SelectorItemModel>): SelectorMergeFunction =
        SingleSelectionMergeFunction(selectionViewModel)

    @Provides
    @SelectionListScreenScope
    @Named(SELECTION_CACHING_FUNCTION)
    fun providesSelectionCachingFunction(): RecentSelectionCachingFunction = IdleRecentSelectionCachingFunction()
}