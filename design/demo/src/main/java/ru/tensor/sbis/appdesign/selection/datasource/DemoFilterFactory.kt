package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoFilter
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import java.util.*

/**
 * @author ma.kolpakov
 */
class DemoFilterFactory : FilterFactory<SelectorItemModel, DemoFilter, Int>{

    override fun createFilter(meta: FilterMeta<SelectorItemModel, Int>): DemoFilter =
        DemoFilter(meta.query, meta.parent?.run(UUID::fromString), meta.pageIndex, meta.itemsOnPage)
}