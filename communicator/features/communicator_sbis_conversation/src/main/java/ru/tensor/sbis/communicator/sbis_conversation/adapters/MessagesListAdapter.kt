package ru.tensor.sbis.communicator.sbis_conversation.adapters

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.BaseConversationAdapter
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.MESSAGE_HOLDER_TYPE
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.list.MessagesListItemAnimator
import ru.tensor.sbis.communicator.sbis_conversation.utils.ConversationViewPool
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewListDiffCallback

/**
 * Адаптер реестра сообщений
 *
 * @property viewPool пул view контента ячейки облачка
 * @property actionsListener слушатель действий, доступных элементам списка
 *
 * @author vv.chekurda
 */
internal class MessagesListAdapter(
    private val viewPool: ConversationViewPool,
    dateUpdater: ListDateViewUpdater,
    private val messageCanBeSwiped: Boolean = true
) : BaseConversationAdapter<ConversationMessage>(dateUpdater) {

    val actionsListener = MessageActionsListenerWrapper()

    override fun getMessagesDiffCallback(
        last: List<MessageViewData>,
        current: List<MessageViewData>
    ): DiffUtil.Callback = MessageViewListDiffCallback(last, current)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<ConversationMessage> =
        if (viewType == MESSAGE_HOLDER_TYPE) {
            MessageViewHolder(
                messageView = viewPool.messageView,
                actionsListener = actionsListener,
                messageCanBeSwiped = messageCanBeSwiped
            )
        } else {
            super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(
        holder: AbstractViewHolder<ConversationMessage>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        // Анимированное раскрытие или закрытие контента в ячейке. Биндить данные заново не нужно
        if (payloads.contains(MessagesListItemAnimator.ExpandPayload)) return
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun createProgressViewHolder(parent: ViewGroup): AbstractViewHolder<ConversationMessage> =
        AbstractViewHolder(
            viewPool.progressView.apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }
        )

    fun changeProgressInRejectButton(show: Boolean, messagePosition: Int) {
        val holder = mRecyclerView?.findViewHolderForAdapterPosition(messagePosition) as? MessageViewHolder
        holder?.changeRejectProgress(show)
    }

    fun changeProgressInAcceptButton(show: Boolean, messagePosition: Int) {
        val holder = mRecyclerView?.findViewHolderForAdapterPosition(messagePosition) as? MessageViewHolder
        holder?.changeAcceptProgress(show)
    }

    override fun clear() {
        super.clear()
        actionsListener.clear()
    }
}