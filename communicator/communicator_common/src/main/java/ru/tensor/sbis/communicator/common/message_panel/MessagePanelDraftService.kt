package ru.tensor.sbis.communicator.common.message_panel

import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.common.CommunicatorCommonPlugin
import ru.tensor.sbis.communicator.generated.DraftMessage
import ru.tensor.sbis.design.message_panel.decl.MessagePanelUseCase
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftService
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase
import ru.tensor.sbis.design.message_panel.domain.QuoteMessageUseCase
import java.util.UUID

/**
 * TODO: 27.06.2022 Добавить документацию
 *
 * @author vv.chekurda
 */
internal class MessagePanelDraftService : MessageDraftService<DraftMessage> {

    val controller by lazy {
        CommunicatorCommonPlugin.messageControllerProvider.getMessageController().get()
    }

    override suspend fun load(useCase: MessagePanelUseCase) = with(useCase) {
        controller.getDraft(conversationUuid)
    }

    override suspend fun save(
        useCase: MessagePanelUseCase,
        draftUuid: UUID,
        recipients: List<UUID>,
        text: String,
        attachments: List<UUID>
    ) = with(useCase) {
        useCase as AbstractMessagePanelUseCase
        val draft = DraftMessage(
            draftUuid,
            System.currentTimeMillis(),
            recipients.asArrayList(),
            text,
            attachments.asArrayList(),
            null,
            null,
            null
        )
        if (this is QuoteMessageUseCase) {
            draft.quotedMessageId = quotingMessageUuid
        }
        controller.saveDraft(conversationUuid, draft)
    }

    override suspend fun save(
        useCase: MessagePanelUseCase,
        draftUuid: UUID,
        recipient: UUID,
        text: String,
        attachments: List<UUID>
    ) {
        TODO("Not yet implemented")
    }
}