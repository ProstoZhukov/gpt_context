package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.action_listeners

import ru.tensor.sbis.design.retail_models.UiTaxSystemCode
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_dangerous.TaxationInfoAccessDangerousApi
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener

/** Общая реализация объекта для обработки действий пользователя с View элементами - "блок СНО". */
internal class TaxationInfoActionListenerHandler(
    private val viewAccessApi: TaxationInfoAccessDangerousApi
) : TaxationInfoActionListenerApi {

    override var onTaxationSystemClickAction: ((taxSystemCode: UiTaxSystemCode) -> Unit)? = null

    override fun setPrimaryTaxationSystemClickListener(action: () -> Unit) {
        viewAccessApi.primaryTaxSystemName.preventDoubleClickListener { action.invoke() }
    }

    override fun setPatentTaxationSystemClickListener(action: () -> Unit) {
        viewAccessApi.patentTaxSystemName.preventDoubleClickListener { action.invoke() }
    }
}