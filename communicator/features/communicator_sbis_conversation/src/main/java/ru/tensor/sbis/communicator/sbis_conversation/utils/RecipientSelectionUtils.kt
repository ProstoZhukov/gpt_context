package ru.tensor.sbis.communicator.sbis_conversation.utils

import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import java.util.*

internal object RecipientSelectionUtils {

    fun getSelectionUseCase(
        conversationUuid: UUID?,
        isNewConversation: Boolean,
        isChat: Boolean,
        canAddParticipant: Boolean,
        conversationType: ConversationType? = null,
        documentUuid: UUID? = null
    ): RecipientSelectionUseCase =
        when {
            isNewConversation ||
                conversationType == ConversationType.VIDEO_CONVERSATION ||
                conversationUuid == null -> {
                RecipientSelectionUseCase.NewDialog
            }

            !isChat -> {
                RecipientSelectionUseCase.Dialog(
                    dialogUuid = conversationUuid,
                    documentUuid = documentUuid
                )
            }

            !canAddParticipant -> {
                RecipientSelectionUseCase.ChatParticipants(chatUuid = conversationUuid)
            }

            else -> {
                RecipientSelectionUseCase.Chat(chatUuid = conversationUuid)
            }
        }
}