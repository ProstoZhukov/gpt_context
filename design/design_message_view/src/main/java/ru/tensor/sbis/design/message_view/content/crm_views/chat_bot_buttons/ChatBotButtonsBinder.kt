package ru.tensor.sbis.design.message_view.content.crm_views.chat_bot_buttons

import android.view.View
import android.view.View.OnLayoutChangeListener
import androidx.core.view.isVisible
import ru.tensor.sbis.design.chips.SbisChipsView
import ru.tensor.sbis.design.chips.api.SbisChipsSelectionDelegate
import ru.tensor.sbis.design.chips.models.SbisChipsCaption
import ru.tensor.sbis.design.chips.models.SbisChipsItem
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.message_view.content.cloud_view.CloudViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.model.ChatBotViewData
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewDataConverter
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import ru.tensor.sbis.design.message_view.utils.castTo

/**
 * Биндер кнопок чат-бота.
 *
 * @author vv.chekurda
 */
internal class ChatBotButtonsBinder(
    converter: MessageViewDataConverter
) : CloudViewContentBinder<View, ChatBotViewData>(converter) {

    private val View.cloudView: CloudView
        get() = findViewById(R.id.design_message_cloud)

    private val View.buttonsView: SbisChipsView
        get() = findViewById(R.id.design_message_view_chat_bot_buttons)

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is ChatBotViewData

    override fun getContent(messageViewPool: MessageViewPool, data: ChatBotViewData): View =
        messageViewPool.chatBotButtonsView

    override fun bindData(
        view: View,
        data: ChatBotViewData,
        listener: MessageViewListener
    ) {
        val needShow = data.chatBotButtonsParams?.needShow == true
        val viewItems = if (needShow) data.getButtons() else emptyList()
        val onButtonsLayoutChangeListener = getButtonsLayoutChangeListener(listener, viewItems)
        if (viewItems.isNotEmpty()) view.buttonsView.addOnLayoutChangeListener(onButtonsLayoutChangeListener)
        bindCloudView(view.cloudView, data, listener)
        view.buttonsView.apply {
            configuration = configuration.copy(
                multiline = true,
                readOnly = data.chatBotButtonsParams?.needDisable ?: true
            )
            items = viewItems
            isVisible = viewItems.isNotEmpty()
            if (listener.check(MessageViewEvent.CRMEvent.OnChatBotButtonClicked::class)) {
                selectionDelegate = object : SbisChipsSelectionDelegate {
                    override fun onChange(selectedItems: List<Int>) = Unit
                    override fun onSelect(id: Int) {
                        items[id].caption?.caption?.let { caption ->
                            listener.onEvent(MessageViewEvent.CRMEvent.OnChatBotButtonClicked(caption.toString()))
                            isVisible = false
                            removeAllButtons()
                        }
                        onDeselect(id)
                    }
                    override fun onDeselect(id: Int) {
                        selectedKeys = selectedKeys.minus(id)
                    }
                }
            }
        }
    }

    private fun ChatBotViewData.getButtons(): List<SbisChipsItem> =
        chatBotButtonsParams?.titles?.mapIndexed { index, title ->
            SbisChipsItem(
                id = index,
                caption = SbisChipsCaption(
                    caption = title
                )
            )
        } ?: emptyList()

    private fun getButtonsLayoutChangeListener(
        listener: MessageViewListener,
        viewItems: List<SbisChipsItem>
    ) = object : OnLayoutChangeListener {
        override fun onLayoutChange(
            buttons: View?,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            buttons?.let {
                if (it.height > 0 && it.castTo<SbisChipsView>()?.items == viewItems) {
                    it.postDelayed(
                        { listener.onEvent(MessageViewEvent.CRMEvent.ScrollToBottom) },
                        DELAY_SCROLL_BOTTOM_AFTER_BUTTONS_DREW
                    )
                    it.removeOnLayoutChangeListener(this)
                }
            }
        }
    }

    private fun SbisChipsView.removeAllButtons() { items = emptyList() }

    override fun setFormattedDateTime(view: View, formattedDateTime: FormattedDateTime) {
        view.cloudView.dateTime = formattedDateTime
    }

    override fun updateSendingState(view: View, sendingState: SendingState) {
        view.cloudView.sendingState = sendingState
    }
}

private const val DELAY_SCROLL_BOTTOM_AFTER_BUTTONS_DREW = 250L
