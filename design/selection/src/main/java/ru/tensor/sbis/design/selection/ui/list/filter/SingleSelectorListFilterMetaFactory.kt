package ru.tensor.sbis.design.selection.ui.list.filter

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.PageDirection
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SingleFilterMeta

/**
 * Создаёт [FilterMeta] для одиночного выбора
 *
 * @author us.bessonov
 */
internal class SingleSelectorListFilterMetaFactory<ANCHOR> :
    SelectorListFilterMetaFactory<ANCHOR> {

    override var selection: List<SelectorItemModel> = emptyList()
        set(value) {
            require(value.size < 2) { "Unexpected multiple items for single selector meta factory $value" }
            field = value
        }

    override var items: List<SelectorItemModel> = emptyList()

    override fun createFilterMeta(
        searchQuery: String,
        anchor: ANCHOR?,
        parentId: SelectorItemId?,
        itemsOnPage: Int,
        pageNumber: Int,
        pageDirection: PageDirection
    ): FilterMeta<SelectorItemModel, ANCHOR> {
        return SingleFilterMeta(
            searchQuery,
            anchor,
            itemsOnPage,
            parentId,
            pageNumber,
            pageDirection
        )
    }
}