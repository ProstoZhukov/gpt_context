package ru.tensor.sbis.design.message_view.content.service_views

import android.view.View
import ru.tensor.sbis.design.cloud_view.content.MessageBlockView
import ru.tensor.sbis.design.cloud_view.content.link.LinkClickListener
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.content.BaseMessageViewContentBinder
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.ServiceMaterialsViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewDataConverter
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Биндер сервисных сообещний с контентом.
 *
 * @author vv.chekurda
 */
internal class ServiceMaterialsContentBinder(
    private val converter: MessageViewDataConverter
) : BaseMessageViewContentBinder<View, ServiceMaterialsViewData>() {

    private val View.messageBlockView: MessageBlockView
        get() = findViewById(R.id.design_message_view_service_message_materials_content)

    private val View.materialsContainer: View
        get() = findViewById(R.id.design_message_view_attachment_item_container)

    private val View.timeView: SbisTextView
        get() = findViewById(R.id.design_message_view_message_materials_item_time)

    private val View.dateView: SbisTextView
        get() = findViewById(R.id.design_message_view_message_materials_item_date)

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is ServiceMaterialsViewData

    override fun getContent(messageViewPool: MessageViewPool, data: ServiceMaterialsViewData): View =
        messageViewPool.serviceMaterialsView

    override fun bindData(
        view: View,
        data: ServiceMaterialsViewData,
        listener: MessageViewListener
    ) {
        val messageBlockView = view.messageBlockView
        val materialsContainer = view.materialsContainer
        val cloudViewData = converter.toCloudComponentViewData(
            data = data,
            listener = listener
        )
        messageBlockView.setLinkClickListener(getLinkClickListener(listener))

        messageBlockView.setMessage(cloudViewData, false)
        setServiceContainerPadding(materialsContainer, data.groupConversation)
        data.formattedDateTime?.also {
            setFormattedDateTime(view, it)
        }
    }

    private fun getLinkClickListener(listener: MessageViewListener): LinkClickListener? =
        if (listener.check(MessageViewEvent.BaseEvent.OnLinkClicked::class)) {
            object : LinkClickListener {
                override fun onLinkClicked() {
                    listener.onEvent(MessageViewEvent.BaseEvent.OnLinkClicked)
                }
            }
        } else {
            null
        }

    override fun setFormattedDateTime(view: View, formattedDateTime: FormattedDateTime) {
        view.timeView.apply {
            visibility = View.VISIBLE
            text = formattedDateTime.time
        }
        view.dateView.apply {
            text = formattedDateTime.date
            visibility = if (!text.isNullOrBlank()) View.VISIBLE else View.GONE
        }
    }

    override fun updateSendingState(view: View, sendingState: SendingState) = Unit
}