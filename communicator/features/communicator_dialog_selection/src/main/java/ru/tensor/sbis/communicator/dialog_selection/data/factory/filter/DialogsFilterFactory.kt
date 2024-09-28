package ru.tensor.sbis.communicator.dialog_selection.data.factory.filter

import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.dialog_selection.data.DialogsFilter
import ru.tensor.sbis.communicator.dialog_selection.presentation.DIALOG_LIST_SIZE
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.MultiFilterMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import javax.inject.Inject

/**
 * Реализация фабрики фильтра для контроллера диалогов
 *
 * @author vv.chekurda
 */
internal class DialogsFilterFactory @Inject constructor()
    : FilterFactory<SelectorItemModel, DialogsFilter, Int> {

    private var lastQuery: String? = null

    override fun createFilter(meta: FilterMeta<SelectorItemModel, Int>): DialogsFilter  {
        val currentSearchQuery = meta.castTo<MultiFilterMeta<SelectorItemModel, Int>>()!!.query
        val needSync = currentSearchQuery != lastQuery
        lastQuery = currentSearchQuery
        return ThemeFilter(
            null,
            null,
            ConversationType.DIALOG,
            null,
            DialogFilter.ALL,
            ChatFilter.ALL,
            ChatType.UNKNOWN,
            meta.castTo<MultiFilterMeta<SelectorItemModel, Int>>()!!.query,
            QueryDirection.TO_OLDER,
            0,
            0,
            null,
            DIALOG_LIST_SIZE,
            true,
            arrayListOf(),
            needSync,
            null
        )
    }
}