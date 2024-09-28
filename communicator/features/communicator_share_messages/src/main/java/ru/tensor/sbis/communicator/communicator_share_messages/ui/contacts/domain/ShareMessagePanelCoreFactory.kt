package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain

import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import java.util.UUID
import javax.inject.Inject

/**
 * Фабрика для создания [CoreConversationInfo] панели сообщения для шаринга.
 *
 * @author vv.chekurda
 */
internal class ShareMessagePanelCoreFactory @Inject constructor() {

    /**
     * Создать [CoreConversationInfo] для идентификатора переписки [conversationUuid].
     */
    fun createCoreConversation(conversationUuid: UUID? = null): CoreConversationInfo =
        CoreConversationInfo(
            conversationUuid = conversationUuid,
            showAttachmentsButton = false,
            showRecipientsPanel = false,
            saveDraftMessage = false,
            loadDraftMessage = false,
            inviteSupported = true,
            sendButtonEnabled = conversationUuid != null,
            minLines = SHARE_MESSAGE_PANEL_MIN_LINES
        )
}

private const val SHARE_MESSAGE_PANEL_MIN_LINES = 3