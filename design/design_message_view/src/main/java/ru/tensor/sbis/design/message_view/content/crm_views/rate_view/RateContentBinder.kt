package ru.tensor.sbis.design.message_view.content.crm_views.rate_view

import android.view.View
import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.R
import ru.tensor.sbis.design.message_view.content.cloud_view.CloudViewContentBinder
import ru.tensor.sbis.design.message_view.listener.MessageViewListener
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.RateCloudViewData
import ru.tensor.sbis.design.message_view.utils.MessageViewDataConverter
import ru.tensor.sbis.design.message_view.utils.MessageViewPool

/**
 * Биндер облачков с рейтингом для чатов тех.поддержки.
 *
 * @author vv.chekurda
 */
internal class RateContentBinder(
    converter: MessageViewDataConverter
) : CloudViewContentBinder<CloudView, RateCloudViewData>(converter) {

    private val View.rateView: MessageRateView
        get() = findViewById(R.id.design_message_view_message_cloud_rate)

    override fun isDataSupported(data: MessageViewData): Boolean =
        data is RateCloudViewData

    override fun getContent(messageViewPool: MessageViewPool, data: RateCloudViewData): CloudView =
        if (data.outgoing) {
            messageViewPool.outcomeRateView
        } else {
            messageViewPool.incomeRateView
        }

    override fun bindData(
        view: CloudView,
        data: RateCloudViewData,
        listener: MessageViewListener
    ) {
        bindCloudView(view, data, listener)
        data.serviceRateData?.also(view.rateView::setData)
    }

    override fun setFormattedDateTime(view: CloudView, formattedDateTime: FormattedDateTime) {
        view.dateTime = formattedDateTime
    }

    override fun updateSendingState(view: CloudView, sendingState: SendingState) {
        view.sendingState = sendingState
    }
}