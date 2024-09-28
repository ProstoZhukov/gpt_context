package ru.tensor.sbis.design.message_view.content.crm_views.greetings_view

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.content.BaseMessageViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.model.GreetingsViewData
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewPool

/**
 * Биндер приветственных кнопок.
 *
 * @author vv.chekurda
 */
internal object GreetingsMessageBinder : BaseMessageViewContentBinder<GreetingsView, GreetingsViewData>() {

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is GreetingsViewData

    override fun getContent(messageViewPool: MessageViewPool, data: GreetingsViewData): GreetingsView =
        messageViewPool.greetingsView

    override fun bindData(
        view: GreetingsView,
        data: GreetingsViewData,
        listener: MessageViewListener
    ) {
        view.setTitles(data.greetings)
        if (listener.check(MessageViewEvent.CRMEvent.OnGreetingClicked::class)) {
            view.setOnGreetingClick { title: String ->
                listener.onEvent(MessageViewEvent.CRMEvent.OnGreetingClicked(title))
            }
        }
    }

    override fun setFormattedDateTime(view: GreetingsView, formattedDateTime: FormattedDateTime) = Unit
    override fun updateSendingState(view: GreetingsView, sendingState: SendingState) = Unit
    override val contentLayoutParams: ViewGroup.LayoutParams
        get() = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
}
