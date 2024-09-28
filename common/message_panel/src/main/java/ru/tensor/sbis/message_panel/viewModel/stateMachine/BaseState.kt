package ru.tensor.sbis.message_panel.viewModel.stateMachine

import ru.tensor.sbis.common.util.statemachine.SessionState
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
abstract class BaseState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    protected val viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
) : SessionState() {
    protected val liveData get() = viewModel.liveData
    protected val recipientsInteractor get() = viewModel.recipientsInteractor
    protected val attachmentsInteractor get() = viewModel.attachmentsInteractor
    protected val messageInteractor get() = viewModel.messageInteractor
    protected val messageResultHelper get() = viewModel.messageResultHelper
    protected val draftInteractor get() = viewModel.draftInteractor
    protected val draftResultHelper get() = viewModel.draftResultHelper
    protected val attachmentPresenter get() = viewModel.attachmentPresenter
    protected val resourceProvider get() = viewModel.resourceProvider

    protected fun loadDraftForNewMessage(
        coreConversationInfo: CoreConversationInfo,
        shouldClearRecipients: Boolean
    ) {
        coreConversationInfo.run {
            fire(
                DraftLoadingStateEvent(
                    document,
                    conversationUuid,
                    isNewConversation,
                    recipients,
                    shouldClearRecipients
                )
            )
        }
    }
}