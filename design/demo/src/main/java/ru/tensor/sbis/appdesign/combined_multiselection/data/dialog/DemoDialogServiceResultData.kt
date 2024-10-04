package ru.tensor.sbis.appdesign.combined_multiselection.data.dialog

import android.text.Spannable
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.design.profile.person.data.PersonData
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogMessageSyncStatus
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogRelevantMessageType
import java.util.*

/**
 * @author ma.kolpakov
 */
data class DemoDialogServiceResultData(
    val id: String,
    val title: String,
    val subtitle: String?,
    val timestamp: Long,
    val syncStatus: SelectionDialogMessageSyncStatus,
    val participantsCollage: List<PersonData>,
    val participantsCount: Int,
    var messageUuid: UUID?,
    val messageType: SelectionDialogRelevantMessageType,
    val messagePersonCompany: String?,
    val messageText: Spannable,
    val isOutgoing: Boolean,
    var isRead: Boolean,
    var isReadByMe: Boolean,
    val isForMe: Boolean,
    val serviceText: Spannable?,
    var unreadCount: Int,
    val documentUuid: UUID?,
    val documentType: DocumentType?,
    val externalEntityTitle: CharSequence?,
    val attachmentCount: Int,
    val isChatForOperations: Boolean,
    val isPrivateChat: Boolean,
    val isSocnetEvent: Boolean,
)
