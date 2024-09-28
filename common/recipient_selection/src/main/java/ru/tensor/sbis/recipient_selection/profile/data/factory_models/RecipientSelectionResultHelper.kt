package ru.tensor.sbis.recipient_selection.profile.data.factory_models

import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import javax.inject.Inject

/**
 * Реализация хэлпера по обработке результата list/refresh микросервиса [RecipientsController]
 * (постраничная загрузка не поддерживается)
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionResultHelper @Inject constructor(
    private val filter: RecipientsSearchFilter
) : ResultHelper<Int, ProfilesFoldersResult> {

    override fun hasNext(result: ProfilesFoldersResult): Boolean = false

    override fun isEmpty(result: ProfilesFoldersResult): Boolean =
        result.isEmpty

    override fun getAnchorForNextPage(result: ProfilesFoldersResult): Int? =
        result.profiles.lastIndex

    override fun getAnchorForPreviousPage(result: ProfilesFoldersResult): Int? = 0
}