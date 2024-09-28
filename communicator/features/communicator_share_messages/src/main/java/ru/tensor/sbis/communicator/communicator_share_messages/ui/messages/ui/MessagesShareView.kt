package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.data.MessagesShareState

/**
 * Контракт view компонента MVI содержимого экрана шаринга в сообщения.
 *
 * @author dv.baranov
 */
internal interface MessagesShareView : MviView<MessagesShareView.Model, MessagesShareView.Event> {

    /** @SelfDocumented */
    sealed interface Event {
        /** Изменился фокус панели ввода сообщения. */
        data class OnMessagePanelFocusChanged(val isFocused: Boolean) : Event

        /** Нажали кнопку отправки. */
        object SendButtonClicked : Event

        /** Изменился текст в панели ввода сообщения. */
        data class OnTextChanged(val newText: CharSequence) : Event
    }

    /** @SelfDocumented */
    data class Model(
        val shareState: MessagesShareState = MessagesShareState.CONVERSATION_SELECTION,
        val isSendButtonEnabled: Boolean = false,
        val selectedConversation: ConversationModel? = null,
        val messagePanelText: String = ""
    )

    /** @SelfDocumented */
    fun interface Factory : (View) -> MessagesShareView

    /** @SelfDocumented */
    fun onKeyboardMeasure(keyboardHeight: Int): Boolean
}
