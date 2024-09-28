package ru.tensor.sbis.communicator.sbis_conversation.ui

import androidx.annotation.IntDef
import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationState
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.NewMessageState
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewState
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewState
import java.util.*

/** Дата-класс состояния экрана переписки */
internal data class ConversationState(
    override var isNewConversation: Boolean = false,
    @NewMessageState
    override var newMessageState: Int = NewMessageState.DEFAULT,
    override var selectedMessage: ConversationMessage? = null,
    override var editedMessage: ConversationMessage? = null,
    @StringRes
    override var missedLoadingErrorRes: Int = 0,
    override var missedLoadingErrorFromController: String? = null,
    @StringRes
    val missedToastErrorRes: Int = 0,
    @GroupChatCreationState
    val groupChatCreationState: Int = GroupChatCreationState.UNDEFINED,
    val addRecipientsToChat: Boolean = false,
    val isChatSettingsShown: Boolean = false,
    val isChoosingRecipients: Boolean = false,
    val keyboardShownForConversation: Boolean = false,
    val ignoreKeyboardEvents: Boolean = true,
    val signActionChosen: SignActions? = null,
    var resendMessageUuid: UUID? = null,
    var isPrivateChatCreation: Boolean = false,
    val isPrivateChat: Boolean = false,
    val audioRecordState: AudioRecordViewState = AudioRecordViewState(),
    val videoRecordState: VideoRecordViewState = VideoRecordViewState(),
    val threadCreationServiceObject: String? = null,
    val isThreadParticipantsSelection: Boolean = false
) : BaseConversationState<ConversationMessage> {

    val isThreadCreation: Boolean
        get() = threadCreationServiceObject != null

    override fun copy(): BaseConversationState<ConversationMessage> {
        return copy(isNewConversation = isNewConversation)
    }
}

@IntDef(
    GroupChatCreationState.UNDEFINED,
    GroupChatCreationState.CHOOSING_RECIPIENTS,
    GroupChatCreationState.CHAT_CREATION
)
@Retention(AnnotationRetention.SOURCE)
internal annotation class GroupChatCreationState {
    companion object {
        const val UNDEFINED = 1
        const val CHOOSING_RECIPIENTS = 2
        const val CHAT_CREATION = 3
    }
}