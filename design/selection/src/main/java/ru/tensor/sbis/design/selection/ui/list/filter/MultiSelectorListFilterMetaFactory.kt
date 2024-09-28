package ru.tensor.sbis.design.selection.ui.list.filter

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.MultiFilterMeta
import ru.tensor.sbis.design.selection.ui.model.PageDirection
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Создаёт [FilterMeta] для множественного выбора
 *
 * @author us.bessonov
 */
internal class MultiSelectorListFilterMetaFactory<ANCHOR> : SelectorListFilterMetaFactory<ANCHOR> {

    override var selection = emptyList<SelectorItemModel>()
    override var items = emptyList<SelectorItemModel>()

    override fun createFilterMeta(
        searchQuery: String,
        anchor: ANCHOR?,
        parentId: SelectorItemId?,
        itemsOnPage: Int,
        pageNumber: Int,
        pageDirection: PageDirection
    ): FilterMeta<SelectorItemModel, ANCHOR> {
        return MultiFilterMeta(
            searchQuery,
            anchor,
            itemsOnPage,
            parentId,
            pageNumber,
            pageDirection,
            selection,
            items
        )
    }
}