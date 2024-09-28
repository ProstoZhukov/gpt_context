package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners

import android.view.View
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous.PaymentTypeAccessDangerousApi
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener

/** Общая реализация объекта для обработки действий пользователя с View элементами - "тип оплаты". */
internal class PaymentTypeActionListenerHandler(
    private val viewAccessApi: PaymentTypeAccessDangerousApi
) : PaymentTypeActionListenerApi {

    override fun setPaymentTypeListener(action: (View) -> Unit) {
        viewAccessApi.paymentTypeButton.preventDoubleClickListener(LONG_CLICK_DELAY) { action.invoke(it) }
    }
}