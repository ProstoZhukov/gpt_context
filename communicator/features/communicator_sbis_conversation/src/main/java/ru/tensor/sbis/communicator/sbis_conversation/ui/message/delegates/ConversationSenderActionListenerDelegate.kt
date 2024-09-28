package ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates

import ru.tensor.sbis.communicator.sbis_conversation.adapters.SenderActionClickListener
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationDataDispatcher
import java.util.UUID

/**
 * Делегат реестра сообщений для работы с именем и фото отправителя.
 *
 * @author da.zhukov
 */
internal class ConversationSenderActionListenerDelegate(
    private val dataDispatcher: ConversationDataDispatcher
) : ConversationMessagesBaseDelegate(),
    SenderActionClickListener {

    /**
     * Признак отображения панели записи аудиосообщения.
     */
    private val isAudioRecordVisible: Boolean
        get() = dataDispatcher.getConversationState().audioRecordState.isVisible

    override fun onPhotoClicked(senderUuid: UUID) {
        if (!isAudioRecordVisible) {
            view?.forceHideKeyboard()
            router?.showProfile(senderUuid)
        } else {
            view?.showCancelRecordingConfirmationDialog()
        }
    }

    override fun onSenderNameClicked(senderUuid: UUID) {
        if (!isAudioRecordVisible) {
            dataDispatcher.addRecipient(senderUuid)
        } else {
            view?.showCancelRecordingConfirmationDialog()
        }
    }
}