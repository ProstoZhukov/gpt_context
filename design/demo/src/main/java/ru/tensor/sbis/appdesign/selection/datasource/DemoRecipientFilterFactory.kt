package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoRecipientFilterFactory : FilterFactory<SelectorItemModel, DemoRecipientFilter, Int> {

    override fun createFilter(meta: FilterMeta<SelectorItemModel, Int>): DemoRecipientFilter =
        DemoRecipientFilter(meta.query, meta.pageIndex, meta.itemsOnPage)
}