package ru.tensor.sbis.communicator.common.data.theme

import android.text.Spannable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.generated.DocumentType
import ru.tensor.sbis.communicator.generated.RelevantMessageType
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.persons.ConversationRegistryItem
import java.util.UUID

/** Дата-класс модели диалога/чата */
data class ConversationModel(
    var uuid: UUID,
    val title: String = StringUtils.EMPTY,
    val titlePostfix: String?,
    val timestamp: Long,
    val favoriteTimestamp: Long?,
    val syncStatus: SyncStatus,
    val participantsCollage: List<PersonData>,
    val participantsCount: Int,
    val participantsUuids: List<UUID>,
    var messageUuid: UUID?,
    val messageType: RelevantMessageType,
    val unreadIconType: ConversationUnreadIconType?,
    val messagePersonName: String?,
    val messagePersonCompany: String?,
    val messageDateSent: Long,
    val messageText: Spannable?,
    val isOutgoing: Boolean,
    var isRead: Boolean,
    var isReadByMe: Boolean,
    val isForMe: Boolean,
    val serviceText: Spannable?,
    var unreadCount: Int,
    var formattedUnreadCount: String,
    val documentUuid: UUID?,
    val documentType: DocumentType?,
    val documentUrl: String?,
    val documentIconText: String?,
    val externalEntityTitle: CharSequence?,
    val attachments: List<AttachmentViewModel>?,
    val attachmentsRegistryModels: List<AttachmentRegisterModel>?,
    val attachmentCount: Int,
    val isChatForView: Boolean,
    val isChatForOperations: Boolean,
    val isPrivateChat: Boolean,
    val isUnreadCountImportant: Boolean,
    val isConversationHiddenOrArchived: Boolean,
    val canBeMarkedUnread: Boolean,
    val canBeMarkedRead: Boolean,
    val isSocnetEvent: Boolean,
    val socnetServiceObject: SocnetServiceObject?,
    val canBeDeleted: Boolean,
    val canBeUndeleted: Boolean,
    val searchHighlights: List<SearchSpan>?,
    val nameHighlights: List<SearchSpan>?,
    val docsHighlights: List<SearchSpan>?,
    val dialogNameHighlights: List<SearchSpan>?,
    val isPinned: Boolean,
    val canBeUnhide: Boolean,
    var isViewed: Boolean,
    val meIsOwner: Boolean,
    val isInMyCompany: Boolean,
    val chatType: ChatType,
    val isGroupConversation: Boolean,
    // счетчик непрочитанных сообщений у канала серый
    val isChatUnreadCounterGray: Boolean = false,
    val dialogTitle: String? = null,
    val folderUuid: UUID? = null,
    val isAuthorBlocked: Boolean = false,
    val photoUrl: String? = null,
    val conversationButtons: List<ConversationButton>? = null,
    val canSendMessage: Boolean = true,
    val noticeData: ThemeNoticeData? = null
) : ConversationRegistryItem {

    /** @SelfDocumented **/
    var formattedDateTime: FormattedDateTime? = null

    /** @SelfDocumented **/
    val compareUuid
        get() = uuid

    /** @SelfDocumented **/
    val isNews: Boolean
        get() = when (documentType) {
            DocumentType.NEWS,
            DocumentType.SOCNET_NEWS,
            DocumentType.SOCNET_NEWS_REPOST -> true
            else                            -> false
        }

    /** @SelfDocumented **/
    val isNotice: Boolean
        get() = noticeData != null

    /**
     * Нужно ли помечать прочитанным сразу по нажатию.
     */
    val markAsReadOnClick: Boolean
        get() = unreadCount > 0 && (isSocnetEvent || isNotice)

    /** @SelfDocumented **/
    fun decrementUnreadCount() {
        unreadCount--
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConversationModel

        if (uuid != other.uuid) return false
        if (title != other.title) return false
        if (timestamp != other.timestamp) return false
        if (syncStatus != other.syncStatus) return false
        if (participantsCollage != other.participantsCollage) return false
        if (participantsCount != other.participantsCount) return false
        if (participantsUuids != other.participantsUuids) return false
        if (messageUuid != other.messageUuid) return false
        if (messageType != other.messageType) return false
        if (messagePersonName != other.messagePersonName) return false
        if (messagePersonCompany != other.messagePersonCompany) return false
        if (messageDateSent != other.messageDateSent) return false
        if (messageText.toString() != other.messageText.toString()) return false
        if (isOutgoing != other.isOutgoing) return false
        if (isRead != other.isRead) return false
        if (isReadByMe != other.isReadByMe) return false
        if (isForMe != other.isForMe) return false
        if (serviceText.toString() != other.serviceText.toString()) return false
        if (unreadCount != other.unreadCount) return false
        if (formattedUnreadCount != other.formattedUnreadCount) return false
        if (documentUuid != other.documentUuid) return false
        if (documentType != other.documentType) return false
        if (documentUrl != other.documentUrl) return false
        if (externalEntityTitle != other.externalEntityTitle) return false
        if (!compareAttachments(attachments, other.attachments)) return false
        if (attachmentCount != other.attachmentCount) return false
        if (isChatForView != other.isChatForView) return false
        if (isChatForOperations != other.isChatForOperations) return false
        if (isPrivateChat != other.isPrivateChat) return false
        if (isUnreadCountImportant != other.isUnreadCountImportant) return false
        if (isConversationHiddenOrArchived != other.isConversationHiddenOrArchived) return false
        if (canBeMarkedUnread != other.canBeMarkedUnread) return false
        if (canBeMarkedRead != other.canBeMarkedRead) return false
        if (isSocnetEvent != other.isSocnetEvent) return false
        if (socnetServiceObject != other.socnetServiceObject) return false
        if (canBeDeleted != other.canBeDeleted) return false
        if (canBeUndeleted != other.canBeUndeleted) return false
        if (searchHighlights != other.searchHighlights) return false
        if (nameHighlights != other.nameHighlights) return false
        if (docsHighlights != other.docsHighlights) return false
        if (dialogNameHighlights != other.dialogNameHighlights) return false
        if (isPinned != other.isPinned) return false
        if (canBeUnhide != other.canBeUnhide) return false
        if (meIsOwner != other.meIsOwner) return false
        if (isViewed != other.isViewed) return false
        if (isChatUnreadCounterGray != other.isChatUnreadCounterGray) return false
        if (dialogTitle != other.dialogTitle) return false
        if (folderUuid != other.folderUuid) return false
        if (isAuthorBlocked != other.isAuthorBlocked) return false
        if (noticeData != other.noticeData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + formattedDateTime.hashCode()
        result = 31 * result + syncStatus.hashCode()
        result = 31 * result + participantsCollage.hashCode()
        result = 31 * result + participantsCount
        result = 31 * result + participantsUuids.hashCode()
        result = 31 * result + messageUuid.hashCode()
        result = 31 * result + messageType.hashCode()
        result = 31 * result + messagePersonName.hashCode()
        result = 31 * result + messagePersonCompany.hashCode()
        result = 31 * result + messageDateSent.hashCode()
        result = 31 * result + messageText?.toString().hashCode()
        result = 31 * result + isOutgoing.hashCode()
        result = 31 * result + isRead.hashCode()
        result = 31 * result + isReadByMe.hashCode()
        result = 31 * result + isForMe.hashCode()
        result = 31 * result + serviceText?.toString().hashCode()
        result = 31 * result + unreadCount
        result = 31 * result + formattedUnreadCount.hashCode()
        result = 31 * result + documentUuid.hashCode()
        result = 31 * result + documentType.hashCode()
        result = 31 * result + documentUrl.hashCode()
        result = 31 * result + externalEntityTitle.hashCode()
        result = 31 * result + (attachments?.sumOf { it.uuid.hashCode() } ?: 0)
        result = 31 * result + attachmentCount
        result = 31 * result + isChatForView.hashCode()
        result = 31 * result + isChatForOperations.hashCode()
        result = 31 * result + isPrivateChat.hashCode()
        result = 31 * result + isUnreadCountImportant.hashCode()
        result = 31 * result + isConversationHiddenOrArchived.hashCode()
        result = 31 * result + canBeMarkedUnread.hashCode()
        result = 31 * result + canBeMarkedRead.hashCode()
        result = 31 * result + isSocnetEvent.hashCode()
        result = 31 * result + socnetServiceObject.hashCode()
        result = 31 * result + canBeDeleted.hashCode()
        result = 31 * result + canBeUndeleted.hashCode()
        result = 31 * result + searchHighlights.hashCode()
        result = 31 * result + nameHighlights.hashCode()
        result = 31 * result + docsHighlights.hashCode()
        result = 31 * result + dialogNameHighlights.hashCode()
        result = 31 * result + isPinned.hashCode()
        result = 31 * result + canBeUnhide.hashCode()
        result = 31 * result + meIsOwner.hashCode()
        result = 31 * result + isViewed.hashCode()
        result = 31 * result + isChatUnreadCounterGray.hashCode()
        result = 31 * result + dialogTitle.hashCode()
        result = 31 * result + folderUuid.hashCode()
        result = 31 * result + isAuthorBlocked.hashCode()
        result = 31 * result + noticeData.hashCode()
        return result
    }

    /**
     * Метод для ручного сравнения списков Attachment, поскольку в Attachment не реализован метод equals
     */
    private fun compareAttachments(first: List<AttachmentViewModel>?, second: List<AttachmentViewModel>?): Boolean {
        if (first == null && second == null) return true
        if (first == null || second == null) return false
        if (first.size != second.size) return false
        first.forEachIndexed { index, attachment ->
            val other = second[index]
            if (attachment.uuid != other.uuid || attachment.fileInfoViewModel != other.fileInfoViewModel) return false
        }
        return true
    }

    /** @SelfDocumented **/
    class SocnetServiceObject(
        val relevantMessageUUID: UUID?,
        private val flat: String,
        val isInviteInGroup: Boolean
    ) {

        override fun toString() = flat

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SocnetServiceObject

            if (relevantMessageUUID != other.relevantMessageUUID) return false
            if (flat != other.flat) return false
            if (isInviteInGroup != other.isInviteInGroup) return false

            return true
        }

        override fun hashCode(): Int {
            var result = relevantMessageUUID?.hashCode() ?: 0
            result = 31 * result + flat.hashCode()
            result = 31 * result + isInviteInGroup.hashCode()
            return result
        }
    }
}
