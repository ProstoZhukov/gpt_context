package ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog

import android.text.SpannableString
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.appdesign.combined_multiselection.data.dialog.DemoDialogServiceResult
import ru.tensor.sbis.appdesign.combined_multiselection.data.dialog.DemoDialogServiceResultData
import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.appdesign.selection.datasource.DemoController
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogMessageSyncStatus
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogRelevantMessageType
import ru.tensor.sbis.design.profile.person.PersonViewData
import ru.tensor.sbis.design.profile.person.data.PersonData
import java.util.*
import kotlin.math.min

/**
 * @author ma.kolpakov
 */
object DemoDialogController : DemoController<DemoDialogServiceResult, DemoRecipientFilter> {

    private val data: List<DemoDialogServiceResultData> = getDataList()

    @Throws(IllegalStateException::class)
    override fun list(filter: DemoRecipientFilter): DemoDialogServiceResult = refresh(filter)

    @Throws(IllegalStateException::class)
    override fun refresh(filter: DemoRecipientFilter): DemoDialogServiceResult =
        when {
            filter.query == "error" -> error("Demo error")
            filter.query.isEmpty()  -> data
            else                    -> data.filter {
                val title = it.title.toLowerCase(Locale.ROOT)
                val query = filter.query.toLowerCase(Locale.ROOT)
                title.contains(query)
            }
        }.getResult(filter.offset, filter.itemsOnPage)

    override fun loadSelectedItems(): DemoDialogServiceResult = DemoDialogServiceResult(emptyList(), false)

    private fun getDataList(): List<DemoDialogServiceResultData> {
        return(0..12).map { number ->
            DemoDialogServiceResultData(
                id = number.toString(),
                title = "Dialog name $number",
                subtitle = StringUtils.EMPTY,
                timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000 * number,
                syncStatus = SelectionDialogMessageSyncStatus.SUCCEEDED,
                participantsCollage = ArrayList((0..1 + number % 3).map { PersonData(UUID.randomUUID()) }),
                participantsCount = 1 + number % 3,
                messageUuid = UUID.randomUUID(),
                messageType = when (number % 2) {
                    0    -> SelectionDialogRelevantMessageType.MESSAGE
                    else -> SelectionDialogRelevantMessageType.SENDING
                },
                messagePersonCompany = if (number % 3 == 0) "Message person company" else null,
                messageText = SpannableString("Message text $number"),
                isOutgoing = number % 2 == 1,
                isRead = number % 2 == 1,
                isReadByMe = number % 2 == 0,
                isForMe = number % 2 == 0,
                serviceText = SpannableString("Service message $number"),
                unreadCount = number,
                documentUuid = UUID.randomUUID(),
                documentType = if (number % 2 == 0) DocumentType.DISC_FOLDER else DocumentType.TASK,
                externalEntityTitle = "Document name $number",
                attachmentCount = 0,
                isChatForOperations = number % 5 == 0,
                isPrivateChat = number % 5 == 1,
                isSocnetEvent = number % 5 == 2
            )
        }
    }

    private fun List<DemoDialogServiceResultData>.getResult(offset: Int, pageSize: Int): DemoDialogServiceResult {
        val begin = offset * pageSize
        val end = begin + pageSize
        val data = subList(offset * pageSize, min((offset + 1) * pageSize, size))
        return DemoDialogServiceResult(data, hasMore = end < size)
    }
}
