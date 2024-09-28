package ru.tensor.sbis.communicator.sbis_conversation.data.mapper

import android.content.Context
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.data.model.ParticipantsData
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.generated.Conversation
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.ContactVMAndProfileMapper.mapPersonToContactVM
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationData
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.util.formatName
import java.util.Date

/**
 * Маппер модели данных о диалоге/канале из модели контроллера [Conversation] в UI [ConversationData].
 *
 * @author vv.chekurda
 */
internal class ConversationDataMapper(
    val context: Context,
    private val mDocumentMapper: DocumentMapper,
    private val mMessageMapper: MessageMapper,
    private val mDateTimeFormatter: ListDateFormatter.DateTimeWithTodayShort,
    coreConversationInfo: CoreConversationInfo?
) {

    private val isInitAsGroupDialog: Boolean =
        coreConversationInfo != null && coreConversationInfo.isInitAsGroupDialog

    private val personalOnlyOptions: ChatNotificationOptions =
        ChatNotificationOptions(
            notificationsTurnedOff = false,
            notificationsPrivateEvents = true,
            notificationsAdminEvents = false
        )

    private val Conversation.pinnedMessage: Message?
        get() = chatPinnedMessage?.let { pinnedMessage ->
            mMessageMapper.apply(pinnedMessage).message?.let { message ->
                if (!message.messageText.isNullOrEmpty()) {
                    message.formattedDateTime = mDateTimeFormatter.format(
                        Date(message.timestampSent),
                        null,
                        false
                    )
                    message
                } else null
            }
        }

    /**
     * Проверка модели контроллера на пустое состояние, которая приходит на пустой кэш.
     * @return true, если модель пустая
     */
    private val Conversation.isModelEmpty: Boolean
        get() = !(participantCount != 0 || hasMore || isFullyCached || isNew)

    fun map(conversation: Conversation, isChat: Boolean) = ConversationData().apply {
        val isConsultation = conversation.chatType == ChatType.CONSULTATION
        isPrivateChat = conversation.chatType == ChatType.PRIVATE
        conversation.chatPermissions.apply {
            canAddParticipant = canAddParticipant && !isPrivateChat
        }
        conversationAccess = ConversationAccess(conversation.chatPermissions, true, !isConsultation, !(isConsultation || isPrivateChat))
        recipients = mapPersonToContactVM(conversation.receivers)
        participants = mapPersonToContactVM(conversation.participants)
        toolbarData = mapToolbarData(conversation, participants!!, recipients!!)
        isClosedChat = conversation.isChatClosed
        isInviteDialog = conversation.isInviteDialog
        unreadCount = conversation.unreadMessagesCount
        isIAmAuthor = conversation.meIsOwner
        document = conversation.document?.let(mDocumentMapper::apply)
        isNotifyPersonalOnly = personalOnlyOptions == conversation.notificationOptions
        isLocked = conversation.isLocked
        canUnpinChatMessage = isChat && conversation.chatPermissions.canUnpinMessage
        pinnedChatMessage = if (isChat) conversation.pinnedMessage else null
        isGroupConversation = when {
            conversation.isModelEmpty -> isInitAsGroupDialog
            isChat -> !isPrivateChat
            else -> !conversation.personalDialog
        }.also { mMessageMapper.isGroupDialog = it }
        isInArchive = conversation.isInArchive
        chatType = conversation.chatType
    }

    private fun mapToolbarData(
        conversation: Conversation,
        participants: List<ContactVM>,
        receivers: List<ContactVM>
    ): ToolbarData {
        val participantList = when {
            conversation.participants.isNotEmpty() -> participants
            conversation.isNew && !conversation.receivers.isNullOrEmpty() -> receivers
            else -> emptyList()
        }

        val participantsData = ParticipantsData(
            participantList,
            participantList.toNameList(),
            conversation.participantCount - participantList.size
        )

        val photoDataList = if (conversation.photoUrl.isNotEmpty()) {
            listOf(PersonData(null, conversation.photoUrl, null))
        } else {
            participantList.toPhotoDataList()
        }
        val isConsultation = conversation.chatType == ChatType.CONSULTATION

        val showOnlyTitle = isConsultation || conversation.isLocked
        val conversationName = conversation.title
        val title = when {
            conversationName.isNotEmpty() -> conversationName
            participants.size == 1 -> participants.first().name.formatName(PersonNameTemplate.SURNAME_NAME)
            else -> participants.joinToString { it.name.formatName(PersonNameTemplate.SURNAME_N) }
        }
        val subtitle: String = when {
            conversation.chatType == ChatType.PRIVATE -> context.resources.getString(R.string.message_panel_channel_private)
            showOnlyTitle || conversation.participantCount == 1 && title.isBlank() -> EMPTY
            else -> context.resources.getQuantityString(
                R.plurals.message_panel_chat_participants,
                conversation.participantCount,
                conversation.participantCount
            )
        }
        val isChat = conversation.administrators.isNotEmpty()

        return ToolbarData(
            photoDataList = photoDataList,
            participantsData = participantsData,
            title = title,
            conversationName = conversationName,
            subtitle = subtitle,
            showOnlyTitle = showOnlyTitle,
            consultationPhoto = if (isConsultation) conversation.photoUrl else null,
            isChat = isChat
        )
    }

    private fun List<ContactVM>.toPhotoDataList(): List<PhotoData> =
        map {
            PersonData(
                it.uuid,
                it.rawPhoto,
                it.initialsStubData?.let { stubData ->
                    InitialsStubData(
                        stubData.initials,
                        stubData.initialsBackgroundColor
                    )
                }
            )
        }

    private fun List<ContactVM>.toNameList(): List<String> =
        if (size == 1) {
            map { it.renderedName }
        } else {
            map { it.name.lastName.trim() }
        }
}