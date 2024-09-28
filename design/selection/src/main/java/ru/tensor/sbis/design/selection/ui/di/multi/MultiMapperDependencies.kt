package ru.tensor.sbis.design.selection.ui.di.multi

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.list.RecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.contract.list.SelectorMergeFunction
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.di.common.ITEMS_MERGE_FUNCTION
import ru.tensor.sbis.design.selection.ui.di.common.SELECTION_CACHING_FUNCTION
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.IdleRecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.utils.MultiRecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.utils.MultiSelectionMergeFunction
import javax.inject.Named

/**
 * @author ma.kolpakov
 */
@Module
internal class MultiMapperDependencies {

    @Provides
    @SelectionListScreenScope
    @Named(ITEMS_MERGE_FUNCTION)
    fun providesMergeFunction(
        selectionViewModel: MultiSelectionViewModel<SelectorItemModel>
    ): SelectorMergeFunction = MultiSelectionMergeFunction(selectionViewModel)

    @Provides
    @SelectionListScreenScope
    @Named(SELECTION_CACHING_FUNCTION)
    fun providesSelectionCachingFunction(
        enableRecentSelectionCaching: Boolean
    ): RecentSelectionCachingFunction = if (enableRecentSelectionCaching) {
        MultiRecentSelectionCachingFunction()
    } else {
        IdleRecentSelectionCachingFunction()
    }
}