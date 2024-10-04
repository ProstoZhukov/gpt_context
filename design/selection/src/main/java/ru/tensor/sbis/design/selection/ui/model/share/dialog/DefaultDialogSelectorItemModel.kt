package ru.tensor.sbis.design.selection.ui.model.share.dialog

import android.text.Spannable
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogMessageSyncStatus
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogRelevantMessageType
import ru.tensor.sbis.edo_decl.document.DocumentType
import java.util.*

/**
 * Реализация по умолчанию для модели диалога в селекторе.
 *
 * @author vv.chekurda
 */
data class DefaultDialogSelectorItemModel(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String?,
    override val dialogTitle: String?,
    override val timestamp: Long,
    override val syncStatus: SelectionDialogMessageSyncStatus,
    override val participantsCollage: List<PersonData>,
    override val participantsCount: Int,
    override var messageUuid: UUID?,
    override val messageType: SelectionDialogRelevantMessageType,
    override val messagePersonCompany: String?,
    override val messageText: Spannable,
    override val isOutgoing: Boolean,
    override var isRead: Boolean,
    override var isReadByMe: Boolean,
    override val isForMe: Boolean,
    override val serviceText: Spannable?,
    override var unreadCount: Int,
    override val documentUuid: UUID?,
    override val documentType: DocumentType?,
    override val externalEntityTitle: CharSequence?,
    override val attachments: List<AttachmentRegisterModel>?,
    override val attachmentCount: Int,
    override val isChatForOperations: Boolean,
    override val isPrivateChat: Boolean,
    override val isSocnetEvent: Boolean,
    override val searchHighlights: List<SearchSpan>? = null,
    override val nameHighlights: List<SearchSpan>? = null,
    override val docsHighlights: List<SearchSpan>? = null,
    override val dialogNameHighlights: List<SearchSpan>? = null
) : DialogSelectorItemModel {
    override lateinit var meta: SelectorItemMeta

    override fun equals(other: Any?): Boolean =
        if (other is DialogSelectorItemModel) {
            id == other.id &&
                title == other.title &&
                subtitle == other.subtitle &&
                dialogTitle == other.dialogTitle &&
                timestamp == other.timestamp &&
                syncStatus == other.syncStatus &&
                participantsCollage == other.participantsCollage &&
                participantsCount == other.participantsCount &&
                messageUuid == other.messageUuid &&
                messageType == other.messageType &&
                messagePersonCompany == other.messagePersonCompany &&
                messageText.toString() == other.messageText.toString() &&
                isOutgoing == other.isOutgoing &&
                isRead == other.isRead &&
                isReadByMe == other.isReadByMe &&
                isForMe == other.isForMe &&
                serviceText.toString() == other.serviceText.toString() &&
                unreadCount == other.unreadCount &&
                documentUuid == other.documentUuid &&
                documentType == other.documentType &&
                externalEntityTitle == other.externalEntityTitle &&
                attachments == other.attachments &&
                attachmentCount == other.attachmentCount &&
                isChatForOperations == other.isChatForOperations &&
                isPrivateChat == other.isPrivateChat &&
                isSocnetEvent == other.isSocnetEvent &&
                searchHighlights == other.searchHighlights &&
                nameHighlights == other.nameHighlights &&
                docsHighlights == other.docsHighlights
        } else super.equals(other)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (subtitle?.hashCode() ?: 0)
        result = 31 * result + (dialogTitle?.hashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + syncStatus.hashCode()
        result = 31 * result + participantsCollage.hashCode()
        result = 31 * result + participantsCount
        result = 31 * result + (messageUuid?.hashCode() ?: 0)
        result = 31 * result + messageType.hashCode()
        result = 31 * result + (messagePersonCompany?.hashCode() ?: 0)
        result = 31 * result + messageText.hashCode()
        result = 31 * result + isOutgoing.hashCode()
        result = 31 * result + isRead.hashCode()
        result = 31 * result + isReadByMe.hashCode()
        result = 31 * result + isForMe.hashCode()
        result = 31 * result + (serviceText?.hashCode() ?: 0)
        result = 31 * result + unreadCount
        result = 31 * result + (documentUuid?.hashCode() ?: 0)
        result = 31 * result + (documentType?.hashCode() ?: 0)
        result = 31 * result + (externalEntityTitle?.hashCode() ?: 0)
        result = 31 * result + (attachments?.hashCode() ?: 0)
        result = 31 * result + attachmentCount
        result = 31 * result + isChatForOperations.hashCode()
        result = 31 * result + isPrivateChat.hashCode()
        result = 31 * result + isSocnetEvent.hashCode()
        result = 31 * result + (searchHighlights?.hashCode() ?: 0)
        result = 31 * result + (nameHighlights?.hashCode() ?: 0)
        result = 31 * result + (docsHighlights?.hashCode() ?: 0)
        result = 31 * result + meta.hashCode()
        return result
    }
}