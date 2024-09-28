package ru.tensor.sbis.communicator.dialog_selection.data.factory

import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionServiceResult
import ru.tensor.sbis.communicator.dialog_selection.data.mapper.isEmpty
import ru.tensor.sbis.list.base.data.ResultHelper
import javax.inject.Inject

/**
 * Реализация хэлпера по обработке результата list/refresh сервисов экрана выбора диалога/участников
 * (постраничная загрузка не поддерживается)
 *
 * @author vv.chekurda
 */
internal class DialogSelectionResultHelper @Inject constructor()
    : ResultHelper<Int, DialogSelectionServiceResult> {

    @Suppress("OverridingDeprecatedMember")
    override fun hasNext(result: DialogSelectionServiceResult): Boolean = false

    override fun isEmpty(result: DialogSelectionServiceResult): Boolean =
        with(result) {
            recipientsResult.isEmpty && dialogsResult.isEmpty()
        }

    override fun getAnchorForNextPage(result: DialogSelectionServiceResult): Int? = null

    override fun getAnchorForPreviousPage(result: DialogSelectionServiceResult): Int? = null
}