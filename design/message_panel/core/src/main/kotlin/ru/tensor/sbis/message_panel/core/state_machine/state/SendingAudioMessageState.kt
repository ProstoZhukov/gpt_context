package ru.tensor.sbis.message_panel.core.state_machine.state

import io.reactivex.Single
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.CleanStateEvent
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import timber.log.Timber

/**
 * TODO: 11/13/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class SendingAudioMessageState(
    viewModel: MessagePanelViewModel,
    private val attachment: Attachment
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    init {
        addOnSetAction { sendNewMessage() }
    }

    private fun sendNewMessage() {
        viewModel.onMessageSending()

        val sendMessageSingle: Single<MESSAGE_SENT_RESULT> =
            viewModel.liveData.draftUuidUpdater.firstOrError().flatMap { draftUuid ->
                interactor.addAttachments(draftUuid, listOf(attachment.uri))
                    .andThen(viewModel.saveDraft(draftUuid))
                    .andThen(
                        messageInteractor.sendMessage(
                            text = null,
                            recipientUuids = liveData.recipientUuidList,
                            documentUuid = liveData.document.value,
                            conversationUuid = liveData.conversationUuid.value,
                            folderUuid = null,
                            signActions = null,
                            quotedMessageUuid = null,
                            answeredMessageUuid = null
                        )
                    )
            }

        disposer.add(sendMessageSingle.subscribe { result ->
            if (messageResultHelper.isSentResultError(result)) {
                Timber.e("Send message failure %s", messageResultHelper.getSentResultError(result))
                liveData.showToast(resourceProvider.getString(R.string.message_panel_error_send_message))
                fire(CleanStateEvent())
                return@subscribe
            }

            viewModel.onMessageSent(result)
            if (viewModel.needToClean()) {
                liveData.postKeyboardEvent(ClosedByRequest)
            }
            viewModel.liveData.resetDraftUuid()
            attachmentPresenter.clearAttachments()
            loadDraftForNewMessage(viewModel.conversationInfo, viewModel.needToClean())
        })
    }
}