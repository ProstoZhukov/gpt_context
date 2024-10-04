package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_safety

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous.PaymentTypeAccessDangerousApi

/** Реализация объекта для безопасного доступа к View элементам "тип оплаты". */
internal class PaymentTypeAccessSafetyHandler(
    private val viewAccessApi: PaymentTypeAccessDangerousApi
) : PaymentTypeAccessSafetyApi {

    override fun setPaymentTypeButtonVisibility(isVisible: Boolean) {
        viewAccessApi.paymentTypeButton.isVisible = isVisible
    }

    override fun setPaymentTypeButtonInvisible(isInvisible: Boolean) {
        viewAccessApi.paymentTypeButton.isInvisible = isInvisible
    }

    override fun setPaymentTypeButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.paymentTypeButton.isEnabled = isEnabled
    }
}