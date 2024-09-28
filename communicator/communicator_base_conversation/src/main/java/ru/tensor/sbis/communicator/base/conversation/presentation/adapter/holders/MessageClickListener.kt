package ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders

import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage

/**
 * Слушатель клика на сообщения.
 *
 * @author da.zhukov
 */
interface MessageClickListener<MESSAGE : BaseConversationMessage> {

    /**@SelfDocumented */
    fun onMessageClicked(conversationMessage: MESSAGE) = Unit
}