package ru.tensor.sbis.message_panel.viewModel

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractor
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import ru.tensor.sbis.message_panel.viewModel.stateMachine.MessagePanelStateMachine

/**
 * Интерфейс зависимостей для работы понели ввода сообщений
 *
 * @author vv.chekurda
 * @since 7/16/2019
 */
interface MessagePanelDependencies<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> {
    val stateMachine: MessagePanelStateMachine<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
    val liveData: MessagePanelLiveData
    val recipientsInteractor: MessagePanelRecipientsInteractor?
    val attachmentsInteractor: MessagePanelAttachmentsInteractor
    val messageInteractor: MessagePanelMessageInteractor<MESSAGE_RESULT, MESSAGE_SENT_RESULT>
    val messageResultHelper: MessageResultHelper<MESSAGE_RESULT, MESSAGE_SENT_RESULT>
    val draftInteractor: MessagePanelDraftInteractor<DRAFT_RESULT>
    val draftResultHelper: DraftResultHelper<DRAFT_RESULT>
    val attachmentPresenter: MessagePanelAttachmentHelper
    val resourceProvider: ResourceProvider
    val recipientsManager: RecipientSelectionResultManager?
}