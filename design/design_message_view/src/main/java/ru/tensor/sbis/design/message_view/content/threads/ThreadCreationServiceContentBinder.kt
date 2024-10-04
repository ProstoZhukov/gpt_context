package ru.tensor.sbis.design.message_view.content.threads

import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.cloud_view.thread.ThreadCreationServiceView
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.content.BaseMessageViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.ThreadEvent
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.ThreadCreationViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.preventDoubleClick

/**
 * Биндер сервисного сообщения о создании треда.
 *
 * @author vv.chekurda
 */
internal object ThreadCreationServiceContentBinder :
    BaseMessageViewContentBinder<ThreadCreationServiceView, ThreadCreationViewData>() {

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is ThreadCreationViewData

    override fun getContent(messageViewPool: MessageViewPool, data: ThreadCreationViewData): ThreadCreationServiceView =
        messageViewPool.threadCreationServiceView

    override fun bindData(
        view: ThreadCreationServiceView,
        data: ThreadCreationViewData,
        listener: MessageViewListener
    ) {
        view.text = data.text
        view.date = data.formattedDateTime?.date
        view.setOnClickListener(
            if (listener.check(ThreadEvent.OnThreadCreationServiceClicked::class)) {
                preventDoubleClick(LONG_CLICK_DELAY) {
                    listener.onEvent(ThreadEvent.OnThreadCreationServiceClicked)
                }
            } else {
                null
            }
        )
        view.setOnLongClickListener(getOnLongClickListener(listener))
    }

    override fun setFormattedDateTime(view: ThreadCreationServiceView, formattedDateTime: FormattedDateTime) {
        view.date = formattedDateTime.date
    }

    override fun updateSendingState(view: ThreadCreationServiceView, sendingState: SendingState) = Unit
}