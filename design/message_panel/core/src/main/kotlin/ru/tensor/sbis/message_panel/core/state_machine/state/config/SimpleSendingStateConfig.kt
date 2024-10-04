package ru.tensor.sbis.message_panel.core.state_machine.state.config

import io.reactivex.disposables.Disposable
import ru.tensor.sbis.message_panel.core.state_machine.config.StateConfig
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.CleanStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.state.AbstractMessagePanelState
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import timber.log.Timber
import java.util.*

/**
 * TODO: 11/13/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class SimpleSendingStateConfig(
    private val quotedMessageUuid: UUID? = null,
    private val signActions: SignActions? = null
) : StateConfig<MessagePanelViewModel, AbstractMessagePanelState<MessagePanelViewModel>> {

    override fun apply(state: AbstractMessagePanelState<MessagePanelViewModel>): Disposable {
        viewModel.onMessageSending()

        val sendMessageSingle = messageInteractor.sendMessage(
            text = liveData.messageText.value,
            recipientUuids = liveData.recipientsUuidList,
            documentUuid = liveData.document.value,
            conversationUuid = liveData.conversationUuid.value,
            folderUuid = liveData.folderUuid.value,
            signActions = signActions,
            quotedMessageUuid = quotedMessageUuid,
            answeredMessageUuid = liveData.answeredMessageUuid.value
        ).cache()

        return sendMessageSingle.subscribe { result ->
            if (messageResultHelper.isSentResultError(result)) {
                showToastWithErrorMessage(messageResultHelper.getSentResultError(result))
                fire(CleanStateEvent(false))
                return@subscribe
            }

            liveData.setMessageText("")
            viewModel.onMessageSent(result)
            if (viewModel.needToClean()) {
                liveData.postKeyboardEvent(ClosedByRequest)
            }
            viewModel.liveData.resetDraftUuid()
            attachmentPresenter.clearAttachments()
            loadDraftForNewMessage(viewModel.conversationInfo, viewModel.needToClean())
        }
    }

    private fun showToastWithErrorMessage(errorMessage: String?) {
        val errorText =
            if (!errorMessage.isNullOrBlank()) {
                errorMessage
            } else {
                resourceProvider.getString(R.string.message_panel_error_send_message)
            }
        liveData.showToast(errorText)
        Timber.e("Send message failure %s", errorText)
    }
}