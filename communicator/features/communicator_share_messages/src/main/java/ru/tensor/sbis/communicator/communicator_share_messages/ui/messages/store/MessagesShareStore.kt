package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.data.MessagesShareState
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate

/**
 * Описывает действия ([Intent]), состояния ([State]) и сайд-эффекты ([Label]) для шаринга в сообщения.
 *
 * @author dv.baranov
 */
internal interface MessagesShareStore :
    Store<MessagesShareStore.Intent, MessagesShareStore.State, MessagesShareStore.Label> {

    /** @SelfDocumented */
    sealed interface Intent {
        /** @SelfDocumented */
        data class InitMenuController(val controller: ShareMenuDelegate) : Intent

        /** Перейти к состоянию выбора переписки из списка. */
        object GoToConversationSelectionState : Intent

        /** Обработать выбранный канал из списка. */
        data class HandleSelectionResult(val result: ConversationModel) : Intent

        // region MessagePanelIntent
        /** Действие на изменения фокуса панели ввода сообщения. */
        data class OnMessagePanelFocusChanged(val isFocused: Boolean) : Intent

        /** Действие на изменение текста панели ввода сообщения. */
        data class OnTextChanged(val newText: CharSequence) : Intent

        /** Действие на нажатие кнопки отправки. */
        object SendButtonClicked : Intent
        // endregionIntent

        /** Навигация назад. */
        object NavigateBack : Intent

        /** Действие при закрытии шаринга пользователем. */
        object ShareClosedByUser : Intent

        /** Завершить шаринг. */
        object EndShare : Intent
    }

    /** @SelfDocumented */
    sealed interface Label {

        /** Завершить шаринг. */
        object EndShare : Label
    }

    /** @SelfDocumented */
    @Parcelize
    data class State(
        val shareState: MessagesShareState = MessagesShareState.CONVERSATION_SELECTION,
        val isSendButtonEnabled: Boolean = false,
        @IgnoredOnParcel val selectedConversation: ConversationModel? = null,
        val messagePanelText: CharSequence = "",
        val isMessagePanelFocused: Boolean = false
    ) : Parcelable
}
