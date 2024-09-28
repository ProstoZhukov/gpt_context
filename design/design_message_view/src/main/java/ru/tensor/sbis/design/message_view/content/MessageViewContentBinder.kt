package ru.tensor.sbis.design.message_view.content

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewPool

/**
 * Биндер [MessageViewData] к соответствующей ячейке.
 *
 * @author vv.chekurda
 */
internal interface MessageViewContentBinder<VIEW : View, DATA : MessageViewData> {

    /** Проверить поддерживает ли данная ячейка переданную дату. */
    fun isDataSupported(data: MessageViewData): Boolean

    /** Получить ячейку, к которой биндим дату. */
    fun getContent(messageViewPool: MessageViewPool, data: DATA): VIEW

    /** @SelfDocumented */
    fun bindData(view: VIEW, data: DATA, listener: MessageViewListener)

    /** @SelfDocumented */
    fun setFormattedDateTime(view: VIEW, formattedDateTime: FormattedDateTime)

    /** @SelfDocumented */
    fun updateSendingState(view: VIEW, sendingState: SendingState)

    /** @SelfDocumented */
    val contentLayoutParams: ViewGroup.LayoutParams
}