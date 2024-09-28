package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationState
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.NewMessageState
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage

/**
 * Дата-класс состояния экрана переписки.
 *
 * @author da.zhukov
 */
internal data class CRMConversationState(
    override var isNewConversation: Boolean = true,
    @NewMessageState
    override var newMessageState: Int = NewMessageState.DEFAULT,
    override var selectedMessage: CRMConversationMessage? = null,
    override var editedMessage: CRMConversationMessage? = null,
    @StringRes
    override var missedLoadingErrorRes: Int = 0,
    override var missedLoadingErrorFromController: String? = null
) : BaseConversationState<CRMConversationMessage> {

    override fun copy(): BaseConversationState<CRMConversationMessage> {
        return copy(isNewConversation = isNewConversation)
    }
}