package ru.tensor.sbis.design.message_view.content

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.model.MessageViewData

/**
 * Базовый класс биндера контента MessageView.
 *
 * @author vv.chekurda.
 */
internal abstract class BaseMessageViewContentBinder<VIEW : View, DATA : MessageViewData> :
    MessageViewContentBinder<VIEW, DATA> {

    override val contentLayoutParams: ViewGroup.LayoutParams
        get() = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    /** @SelfDocumented */
    protected fun getOnLongClickListener(listener: MessageViewListener): View.OnLongClickListener? =
        if (listener.check(MessageViewEvent.BaseEvent.OnLongClicked::class)) {
            View.OnLongClickListener { view ->
                listener.onEvent(MessageViewEvent.BaseEvent.OnLongClicked(view))
                true
            }
        } else {
            null
        }

    /** @SelfDocumented */
    protected fun getOnClickListener(listener: MessageViewListener): View.OnClickListener? =
        if (listener.check(MessageViewEvent.BaseEvent.OnClicked::class)) {
            View.OnClickListener { _ ->
                listener.onEvent(MessageViewEvent.BaseEvent.OnClicked)
            }
        } else {
            null
        }
}