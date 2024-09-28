package ru.tensor.sbis.design.selection.ui.list.filter

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.PageDirection
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Предназначен для создания [FilterMeta], используемой при формировании фильтра
 *
 * @author us.bessonov
 */
internal interface SelectorListFilterMetaFactory<ANCHOR> {

    var selection: List<SelectorItemModel>
    var items: List<SelectorItemModel>

    /**
     * Создаёт специфичную реализацию [FilterMeta]
     */
    fun createFilterMeta(
        searchQuery: String,
        anchor: ANCHOR?,
        parentId: SelectorItemId?,
        itemsOnPage: Int,
        pageNumber: Int,
        pageDirection: PageDirection
    ): FilterMeta<SelectorItemModel, ANCHOR>
}
