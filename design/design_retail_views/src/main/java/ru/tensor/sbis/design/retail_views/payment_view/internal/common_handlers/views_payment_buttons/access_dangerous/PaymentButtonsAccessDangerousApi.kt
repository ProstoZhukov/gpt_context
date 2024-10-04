package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_views.double_button.DoubleButtonApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Обобщение API для доступа к View элементам "кнопки оплаты". */
interface PaymentButtonsAccessDangerousApi {

    /** Получение прямого доступа к кнопке "Оплата". */
    val checkButton: SbisButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к кнопке "Оплата" с вводом значения оплачиваемого/вносимого значения. */
    val checkDoubleButton: DoubleButtonApi
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к кнопке "Оплата картой". */
    val payCardButton: DoubleButtonApi
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /**
     * Получение прямого доступа фиктивной View - "Привязка кнопки оплаты".
     *
     * ВАЖНО: View доступна только на экране оплаты и только в телефонной верстке.
     * Требуется для обеспечения "хитрой" привязки элементов в случае отсутствия
     * кнопки "Оплата картой".
     * https://online.sbis.ru/opendoc.html?guid=12a09896-f84b-4e45-ba44-e2839cb05db4&client=3
     */
    val dummyPaymentButtonGuidelinePortLayoutView: View?
        @DangerousApi get
}