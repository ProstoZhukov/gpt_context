package ru.tensor.sbis.design.selection.ui.di.common

import android.os.Bundle
import dagger.Module
import dagger.Provides
import io.reactivex.functions.BiFunction
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.contract.list.RecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.contract.list.ResultMapper
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.list.ListItemMapper
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.showDividers
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.ChooseAllFixedButtonViewModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Named

typealias FilterFunction = BiFunction<List<SelectorItemModel>, String, List<SelectorItemModel>>

/**
 * @see ResultMapper.mergeFunction
 */
internal const val ITEMS_MERGE_FUNCTION = "ITEMS_MERGE_FUNCTION"

/**
 * @see ResultMapper.recentSelectionCachingFunction
 */
internal const val SELECTION_CACHING_FUNCTION = "SELECTION_CACHING_FUNCTION"

/**
 * @author ma.kolpakov
 */
@Module
internal class MapperModule {

    @Provides
    @SelectionListScreenScope
    fun provideListItemMapper(
        viewHolderHelpers: MutableMap<Any, ViewHolderHelper<SelectorItemModel, *>>,
        clickDelegate: SelectionClickDelegate<SelectorItemModel>,
        selectorCustomisation: SelectorCustomisation
    ) = ListItemMapper(selectorCustomisation, viewHolderHelpers, clickDelegate)

    @Provides
    @SelectionListScreenScope
    fun provideResultMapper(
        listMapper: ListMapper<Any, SelectorItemModel>,
        listItemMapper: ListItemMapper,
        metaFactory: ItemMetaFactory,
        chooseAllButtonViewModel: ChooseAllFixedButtonViewModel?,
        filterFunction: FilterFunction,
        @Named(ITEMS_MERGE_FUNCTION)
        mergeFunction: BiFunction<
            List<SelectorItemModel>,
            List<SelectorItemModel>,
            List<SelectorItemModel>
            >,
        @Named(SELECTION_CACHING_FUNCTION)
        selectionCachingFunction: RecentSelectionCachingFunction,
        arguments: Bundle
    ): ResultMapper<Any> =
        ResultMapper(
            listMapper,
            listItemMapper,
            metaFactory,
            chooseAllButtonViewModel,
            filterFunction,
            mergeFunction,
            selectionCachingFunction,
            arguments.showDividers
        )
}
