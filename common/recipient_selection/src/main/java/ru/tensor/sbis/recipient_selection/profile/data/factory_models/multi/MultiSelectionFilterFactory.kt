package ru.tensor.sbis.recipient_selection.profile.data.factory_models.multi

import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.MultiFilterMeta
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.ui.RECIPIENT_SELECTION_LIST_SIZE
import ru.tensor.sbis.recipient_selection.profile.ui.castTo

/**
 * Фабрика фильтра множественного выбора получателей
 *
 * @author vv.chekurda
 */
internal class MultiSelectionFilterFactory(
    private val filter: RecipientsSearchFilter
) : FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int> {

    private var lastSearchQuery: String = filter.searchString

    override fun createFilter(meta: FilterMeta<RecipientSelectorItemModel, Int>): RecipientsSearchFilter =
        meta.castTo<MultiFilterMeta<RecipientSelectorItemModel, Int>>()!!.run {
            filter.copy(
                searchString = query,
                excludeList = if (lastSearchQuery == query) selection.map { it.id } else emptyList(),
                count = RECIPIENT_SELECTION_LIST_SIZE
            ).also {
                lastSearchQuery = query
            }
        }
}