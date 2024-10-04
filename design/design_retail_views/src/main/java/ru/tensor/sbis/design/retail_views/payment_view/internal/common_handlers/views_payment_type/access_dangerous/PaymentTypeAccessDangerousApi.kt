package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous

import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Обобщение API для доступа к View элементам "тип оплаты". */
interface PaymentTypeAccessDangerousApi {

    /** Получение прямого доступа к кнопке "Тип оплаты". */
    val paymentTypeButton: SbisButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}