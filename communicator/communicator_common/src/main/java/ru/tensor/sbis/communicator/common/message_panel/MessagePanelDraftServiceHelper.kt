package ru.tensor.sbis.communicator.common.message_panel

import ru.tensor.sbis.communicator.common.CommunicatorCommonPlugin
import ru.tensor.sbis.communicator.generated.DraftMessage
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftServiceHelper

/**
 * TODO: 27.06.2022 Добавить документацию
 *
 * @author vv.chekurda
 */
internal class MessagePanelDraftServiceHelper : MessageDraftServiceHelper<DraftMessage> {

    val controller by lazy { CommunicatorCommonPlugin.messageControllerProvider.getMessageController().get() }

    override suspend fun getId(draft: DraftMessage) =
        draft.id

    override suspend fun isEmpty(draft: DraftMessage) =
        controller.isDraftEmpty(draft)

    override suspend fun getText(draft: DraftMessage) =
        draft.text

    override suspend fun getRecipients(draft: DraftMessage) =
        draft.recipients

    override suspend fun getQuoteContent(draft: DraftMessage) =
        // TODO: загрузить цитату
        null

    override suspend fun getAnsweredMessageId(draft: DraftMessage) =
        draft.answer
}