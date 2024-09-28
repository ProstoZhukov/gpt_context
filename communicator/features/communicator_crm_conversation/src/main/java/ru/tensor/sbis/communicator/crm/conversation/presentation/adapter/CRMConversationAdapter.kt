package ru.tensor.sbis.communicator.crm.conversation.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.BaseConversationAdapter
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.MESSAGE_HOLDER_TYPE
import ru.tensor.sbis.communicator.crm.conversation.data.model.*
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.MessageViewListDiffCallback
import ru.tensor.sbis.design.message_view.utils.MessageViewPool

/**
 * Адаптер реестра сообщений.
 *
 * @property messageViewPool пул для MessageView.
 * @property actionsListener слушатель действий, доступных элементам списка.
 * @property isOperator true, если чат открыт со стороны оператора, false иначе.
 * @property dateUpdater форматтер даты.
 *
 * @author da.zhukov
 */
internal class CRMConversationAdapter(
    private val messageViewPool: MessageViewPool,
    private val actionsListener: CRMMessageActionsListener,
    private val isOperator: Boolean,
    dateUpdater: ListDateViewUpdater
) : BaseConversationAdapter<CRMConversationMessage>(dateUpdater) {

    override fun getMessagesDiffCallback(
        last: List<MessageViewData>,
        current: List<MessageViewData>
    ): DiffUtil.Callback = MessageViewListDiffCallback(last, current)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<CRMConversationMessage> =
        if (viewType == MESSAGE_HOLDER_TYPE) {
            CRMMessageViewHolder(
                messageView = MessageView(parent.context).apply {
                    setMessageViewPool(messageViewPool)
                },
                actionsListener = actionsListener,
                isOperator = isOperator
            )
        } else {
            super.onCreateViewHolder(parent, viewType)
        }
}