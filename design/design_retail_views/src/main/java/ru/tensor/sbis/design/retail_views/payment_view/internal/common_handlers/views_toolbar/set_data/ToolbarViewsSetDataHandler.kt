package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data

import android.content.Context
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi

/** Реализация объекта для установки данных в элементы управления "Ввод денежных средств". */
internal class ToolbarViewsSetDataHandler(
    private val viewAccessApi: ToolbarViewsAccessDangerousApi
) : ToolbarViewsSetDataApi {

    private val context: Context
        get() = viewAccessApi.clientButton.context

    override fun setClientName(clientName: String?) {
        (clientName ?: context.getString(R.string.retail_views_payment_client_btn_text))
            .let { clientNameOrStub -> viewAccessApi.clientButton.setTitle(clientNameOrStub) }
    }
}