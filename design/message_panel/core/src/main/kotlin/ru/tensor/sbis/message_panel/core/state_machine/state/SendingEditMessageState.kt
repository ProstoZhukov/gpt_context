package ru.tensor.sbis.message_panel.core.state_machine.state

import ru.tensor.sbis.message_panel.core.state_machine.event.transition.CleanStateEvent
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import timber.log.Timber
import java.util.*

/**
 * TODO: 11/13/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class SendingEditMessageState(
    viewModel: MessagePanelViewModel,
    private val messageUuid: UUID
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {
    init {
        addOnSetAction { sendEditedMessage() }
    }

    private fun sendEditedMessage() {
        viewModel.onMessageSending()

        disposer.add(messageInteractor.editMessage(messageUuid, liveData.messageText.value ?: "").subscribe {
            if (messageResultHelper.isResultError(it)) {
                Timber.e("Editing failure %s", messageResultHelper.getResultError(it))
                liveData.showToast(resourceProvider.getString(R.string.message_panel_comment_edit_message))
                fire(CleanStateEvent())
                return@subscribe
            }

            liveData.setMessageText("")
            viewModel.liveData.resetDraftUuid()
            attachmentPresenter.clearAttachments()
            if (viewModel.needToClean()) {
                liveData.postKeyboardEvent(ClosedByRequest)
            }
            viewModel.onMessageEdit(it)
            loadDraftForNewMessage(viewModel.conversationInfo, viewModel.needToClean())
        })
    }
}