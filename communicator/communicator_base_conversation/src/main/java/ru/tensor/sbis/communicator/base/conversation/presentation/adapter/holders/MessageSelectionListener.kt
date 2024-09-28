package ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders

import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage

/**
 * Слушатель выбранного сообщения.
 *
 * @author vv.chekurda
 */
interface MessageSelectionListener<MESSAGE : BaseConversationMessage> {

    /**@SelfDocumented */
    fun onMessageSelected(conversationMessage: MESSAGE)
}