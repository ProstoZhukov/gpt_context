package ru.tensor.sbis.design.message_view.content.service_views

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import ru.tensor.sbis.common_views.CustomLinkMovementMethod
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.content.BaseMessageViewContentBinder
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.EmojiType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.StarType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ThumbType
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.CRMEvent.OnRateRequestButtonClicked
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent.ServiceEvent.OnServiceMessageClicked
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.ServiceViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Биндер сервисных сообщений.
 *
 * @author vv.chekurda
 */
internal object ServiceContentBinder : BaseMessageViewContentBinder<View, ServiceViewData>() {

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is ServiceViewData

    override fun getContent(messageViewPool: MessageViewPool, data: ServiceViewData): View =
        messageViewPool.serviceMessageView

    override fun bindData(
        view: View,
        data: ServiceViewData,
        listener: MessageViewListener
    ) {
        val context = view.context
        val serviceTextView: TextView = view.findViewById(R.id.design_message_view_service_message_text)
        val serviceIconView: SbisTextView = view.findViewById(R.id.design_message_view_service_message_icon)
        val timeView: SbisTextView = view.findViewById(R.id.design_message_view_message_item_time)
        val dateView: SbisTextView = view.findViewById(R.id.design_message_view_message_item_date)
        val serviceContainer: RelativeLayout = view.findViewById(R.id.design_message_view_message_item_container)
        val unfoldedGroupIconMarginTop: Int = Offset.X3S.getDimenPx(context)

        setServiceContainerPadding(serviceContainer, data.groupConversation)
        data.clickableSpan?.callback = object : ChatSpanCallback {
            override fun onClick() {
                listener.onEvent(OnServiceMessageClicked)
            }
        }

        view.setOnClickListener {
            if (data.isServiceGroup) listener.onEvent(OnServiceMessageClicked)
        }
        view.setOnLongClickListener(getOnLongClickListener(listener))

        serviceTextView.movementMethod = CustomLinkMovementMethod.instance
        serviceTextView.text = getCrmServiceMessageText(context, data)

        if (data.isServiceGroup) {
            dateView.visibility = View.GONE
            timeView.visibility = View.INVISIBLE
            serviceIconView.visibility = View.VISIBLE
            var iconText = if (data.isFoldedServiceMessageGroup) {
                context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_arrow_right)
            } else {
                context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_arrow_down)
            }
            // kostyl' https://online.sbis.ru/open_dialog.html?guid=4e3b10b2-c9cc-4452-9e47-dd5b86cbb609&message=75c50f4f-e244-4d54-ba5d-dfc74e6b15ff
            serviceIconView.let { iconView ->
                (iconView.layoutParams as RelativeLayout.LayoutParams).topMargin =
                    if (data.isFoldedServiceMessageGroup) 0 else unfoldedGroupIconMarginTop
            }
            if (!data.isFoldedServiceMessageGroup &&
                data.serviceMessageGroup!!.messagesCount > data.serviceMessageGroup.unfoldedMessagesLimit
            ) {
                iconText += MORE_SERVICE_MESSAGES_SIGN
            }
            serviceIconView.text = iconText
        } else {
            serviceIconView.visibility = View.GONE
            data.formattedDateTime?.also {
                setFormattedDateTime(view, it)
            }
        }

        data.rateServiceMessage?.let { message ->
            view.findViewById<SbisButton>(R.id.design_message_view_service_rate_request_button)?.apply {
                setIcon(
                    when (message.consultationRateType) {
                        is StarType -> SbisMobileIcon.Icon.smi_navBarFavorite
                        is EmojiType -> SbisMobileIcon.Icon.smi_EmoiconSmile
                        is ThumbType -> SbisMobileIcon.Icon.smi_ThumbUp2
                    }
                )
                isVisible = message.requestIsActive
                setOnClickListener {
                    listener.onEvent(OnRateRequestButtonClicked(message.consultationRateType, message.disableComment))
                }
            }
        }
    }

    override fun setFormattedDateTime(view: View, formattedDateTime: FormattedDateTime) {
        val timeView: SbisTextView = view.findViewById(R.id.design_message_view_message_item_time)
        val dateView: SbisTextView = view.findViewById(R.id.design_message_view_message_item_date)
        val serviceIconView: SbisTextView = view.findViewById(R.id.design_message_view_service_message_icon)
        if (serviceIconView.isVisible) return
        timeView.apply {
            visibility = View.VISIBLE
            text = formattedDateTime.time
        }
        dateView.apply {
            text = formattedDateTime.date
            visibility = if (!text.isNullOrBlank()) View.VISIBLE else View.GONE
        }
    }

    override fun updateSendingState(view: View, sendingState: SendingState) = Unit
}

private const val MORE_SERVICE_MESSAGES_SIGN = " \n. . ."