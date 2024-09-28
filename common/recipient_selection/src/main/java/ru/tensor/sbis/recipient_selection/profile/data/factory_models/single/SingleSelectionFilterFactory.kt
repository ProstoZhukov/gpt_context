package ru.tensor.sbis.recipient_selection.profile.data.factory_models.single

import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter

/**
 * Фабрика фильтра одиночного выбора получателей
 *
 * @author vv.chekurda
 */
internal class SingleSelectionFilterFactory(
    private val filter: RecipientsSearchFilter
) : FilterFactory<RecipientSelectorItemModel, RecipientsSearchFilter, Int> {

    override fun createFilter(meta: FilterMeta<RecipientSelectorItemModel, Int>): RecipientsSearchFilter =
        filter.copy(searchString = meta.query)
}