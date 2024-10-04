package ru.tensor.sbis.design.message_view.content.message

import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.content.cloud_view.CloudViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.model.MessageCloudViewData
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewDataConverter
import ru.tensor.sbis.design.message_view.utils.MessageViewPool

/**
 * Биндер обычных сообщений.
 *
 * @author vv.chekurda
 */
internal class MessageCloudViewContentBinder(
    converter: MessageViewDataConverter
) : CloudViewContentBinder<CloudView, MessageCloudViewData>(converter) {

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is MessageCloudViewData

    override fun getContent(messageViewPool: MessageViewPool, data: MessageCloudViewData): CloudView =
        if (data.outgoing) {
            messageViewPool.outcomeCloudView
        } else {
            messageViewPool.incomeCloudView
        }

    override fun bindData(view: CloudView, data: MessageCloudViewData, listener: MessageViewListener) {
        bindCloudView(view, data, listener)
    }

    override fun setFormattedDateTime(view: CloudView, formattedDateTime: FormattedDateTime) {
        view.dateTime = formattedDateTime
    }

    override fun updateSendingState(view: CloudView, sendingState: SendingState) {
        view.sendingState = sendingState
    }
}