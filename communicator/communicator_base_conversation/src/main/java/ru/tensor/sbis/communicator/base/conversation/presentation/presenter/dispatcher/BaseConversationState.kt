package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher

import androidx.annotation.IntDef
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage

/**
 * Базовые состояния сообщений переписки.
 *
 * @author vv.chekurda
 */
interface BaseConversationState<MESSAGE : BaseConversationMessage> {
    var isNewConversation: Boolean

    @NewMessageState
    var newMessageState: Int
    var selectedMessage: MESSAGE?
    var editedMessage: MESSAGE?
    var missedLoadingErrorRes: Int
    var missedLoadingErrorFromController: String?

    fun copy(): BaseConversationState<MESSAGE>
}

@IntDef(NewMessageState.DEFAULT, NewMessageState.EDITING, NewMessageState.SENDING, NewMessageState.QUOTING)
@Retention(AnnotationRetention.SOURCE)
annotation class NewMessageState {
    companion object {
        const val DEFAULT = 1
        const val EDITING = 2
        const val SENDING = 3
        const val QUOTING = 4
    }
}