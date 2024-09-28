package ru.tensor.sbis.design.message_view.content.threads

import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.cloud_view.thread.CloudThreadView
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.content.BaseMessageViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.ThreadEvent
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.ThreadViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.preventDoubleClick

/**
 * Биндер треда в облачке.
 *
 * @author vv.chekurda
 */
internal object ThreadContentBinder : BaseMessageViewContentBinder<CloudThreadView, ThreadViewData>() {

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is ThreadViewData

    override fun getContent(messageViewPool: MessageViewPool, data: ThreadViewData): CloudThreadView =
        messageViewPool.cloudThreadView

    override fun bindData(
        view: CloudThreadView,
        data: ThreadViewData,
        listener: MessageViewListener
    ) {
        view.data = data.threadData
        view.setOnClickListener(
            if (listener.check(ThreadEvent.OnThreadMessageClicked::class)) {
                preventDoubleClick(LONG_CLICK_DELAY) {
                    listener.onEvent(ThreadEvent.OnThreadMessageClicked)
                }
            } else {
                null
            }
        )
        view.setOnLongClickListener(getOnLongClickListener(listener))
    }

    override fun setFormattedDateTime(view: CloudThreadView, formattedDateTime: FormattedDateTime) = Unit

    override fun updateSendingState(view: CloudThreadView, sendingState: SendingState) = Unit
}