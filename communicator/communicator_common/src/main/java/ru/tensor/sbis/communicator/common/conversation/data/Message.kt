package ru.tensor.sbis.communicator.common.conversation.data

import android.text.Spannable
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaMessageData
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaType
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.communicator.generated.MessageRemovableType
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.profiles.generated.PersonName
import java.util.*

/**
 * Дата-класс сообщений
 * Created by gs.raudiyaynen on 20.07.2017.
 */
data class Message(
    val uuid: UUID,
    val timestamp: Long,
    var syncStatus: SyncStatus,
    val timestampSent: Long,
    val outgoing: Boolean,
    val forMe: Boolean,
    val removableType: MessageRemovableType,
    val editable: Boolean,
    val canCreateThread: Boolean,
    val isQuotable: Boolean,
    val edited: Boolean,
    @Deprecated("use readByMe or readByReceiver") var read: Boolean,
    val receiverCount: Int,
    val senderViewData: PersonData,
    val senderName: PersonName,
    val receiverName: String?,
    val receiverLastName: String?,
    val content: ArrayList<MessageContentItem>,
    val rootElements: ArrayList<Int>,
    val attachmentCount: Int,
    val isDisabledStyle: Boolean,
    val isDownscaledImages: Boolean,
    val pinnable: Boolean,
    val timestampRead: Long?,
    val timestampReadByMe: Long?,
    val messageText: Spannable?,
    var readByMe: Boolean,
    val readByReceiver: Boolean,
    var mediaMessageData: MediaMessageData?,
    val threadInfo: ThreadInfo? = null,
    val isAuthorBlocked: Boolean = false,
) {

    /** @SelfDocumented **/
    var formattedDateTime: FormattedDateTime? = null

    /** @SelfDocumented **/
    val sendingState: SendingState
        get() = when {
            syncStatus == SyncStatus.ERROR   -> SendingState.NEEDS_MANUAL_SEND
            syncStatus == SyncStatus.SENDING -> SendingState.SENDING
            readByReceiver                   -> SendingState.IS_READ
            else                             -> SendingState.SENT
        }

    /** @SelfDocumented **/
    val textForCopy: CharSequence?
        get() = if (mediaMessageData != null) {
            mediaMessageData?.recognizedText
                ?.takeIf { mediaMessageData?.recognized == true || edited }
        } else {
            messageText
        }

    /**
     * Признак типа аудиосообщения.
     */
    val isAudioMessage: Boolean
        get() = mediaMessageData?.type == MediaType.AUDIO

    /**
     * Признак типа видеосообщения.
     */
    val isVideoMessage: Boolean
        get() = mediaMessageData?.type == MediaType.VIDEO

    /**
     * Признак типа медиа сообщение.
     */
    val isMediaMessage: Boolean
        get() = isAudioMessage || isVideoMessage

    /**
     * Сообщение - комментарий по совещанию (смогу, не смогу и т.д.)
     */
    var isMeetingInviteAnswer: Boolean = false

    /**
     * Вернуть текущее сообщение с изменененными полями, которые отвечают за состояние отправки.
     * Значение полей берется из другого сообщения - [other], для того чтобы определить изменился только статус отправки или нет.
     */
    @Suppress("unused")
    fun copySendingState(other: Message): Message =
        copy(
            syncStatus = other.syncStatus,
            readByReceiver = other.readByReceiver,
            // При отправке сообщения изначально removableFromArchive == false, игнорируем это поле при сравнении
            removableType = other.removableType,
            // При отправке сообщения timestamp изначально == 0, игнорируем поле при сравнении
            timestamp = other.timestamp
        )

    override fun toString(): String {
        return "Message{" +
                "mUuid=" + uuid +
                ", mTimestamp=" + timestamp +
                ", mSyncStatus=" + syncStatus +
                ", mTimestampSent=" + timestampSent +
                ", mOutgoing=" + outgoing +
                ", mForMe=" + forMe +
                ", removableType=" + removableType +
                ", mEditable=" + editable +
                ", mQuotable=" + isQuotable +
                ", mEdited=" + edited +
                ", mReceiverCount=" + receiverCount +
                ", mSenderViewData=" + senderViewData +
                ", mSenderName=" + senderName +
                ", mReceiverName='" + receiverName + '\'' +
                ", mReceiverLastName='" + receiverLastName + '\'' +
                ", mContent=" + content +
                ", mRootElements=" + rootElements +
                ", mAttachmentCount=" + attachmentCount +
                ", mDisabledStyle=" + isDisabledStyle +
                ", mDownscaledImages=" + isDownscaledImages +
                ", mTimestampRead=" + timestampRead +
                ", mTimestampReadByMe=" + timestampReadByMe +
                ", mMessageText=" + messageText +
                ", mIsAuthorBlocked=" + isAuthorBlocked +
                '}'
    }

}