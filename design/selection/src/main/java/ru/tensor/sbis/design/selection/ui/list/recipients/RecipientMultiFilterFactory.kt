package ru.tensor.sbis.design.selection.ui.list.recipients

import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.MultiFilterMeta
import ru.tensor.sbis.design.selection.ui.model.SingleFilterMeta
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel

/**
 * Реализация [FilterFactory] для работы с фильтрами получателей
 *
 * @author ma.kolpakov
 */
internal class RecipientMultiFilterFactory<DATA : RecipientSelectorItemModel> :
    FilterFactory<DATA, RecipientFilter<DATA>, Int> {

    override fun createFilter(meta: FilterMeta<DATA, Int>): RecipientFilter<DATA> = when (meta) {
        is SingleFilterMeta -> error("Unexpected meta type")
        is MultiFilterMeta -> RecipientFilter(LinkedHashSet(meta.selection), meta.items, meta.query)
    }
}