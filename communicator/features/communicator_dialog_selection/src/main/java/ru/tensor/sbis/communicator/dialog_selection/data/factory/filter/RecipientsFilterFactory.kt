package ru.tensor.sbis.communicator.dialog_selection.data.factory.filter

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.dialog_selection.data.RecipientsFilter
import ru.tensor.sbis.communicator.dialog_selection.presentation.RECIPIENT_LIST_SIZE
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.MultiFilterMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import javax.inject.Inject

/**
 * Реализация фабрики фильтра для контроллера получателей
 *
 * @author vv.chekurda
 */
internal class RecipientsFilterFactory @Inject constructor()
    : FilterFactory<SelectorItemModel, RecipientsFilter, Int> {

    private var lastSearchQuery: String = StringUtils.EMPTY

    override fun createFilter(meta: FilterMeta<SelectorItemModel, Int>): RecipientsFilter =
        meta.castTo<MultiFilterMeta<SelectorItemModel, Int>>()!!.run {
            RecipientsFilter(
                searchString = query,
                excludeList = if (lastSearchQuery == query) selection.map { it.id } else emptyList(),
                count = RECIPIENT_LIST_SIZE
            ).also {
                lastSearchQuery = query
            }
        }
}