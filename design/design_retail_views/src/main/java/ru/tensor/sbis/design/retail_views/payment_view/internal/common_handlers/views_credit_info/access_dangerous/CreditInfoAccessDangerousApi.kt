package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous

import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Обобщение API для доступа к View элементам "информация о долге". */
interface CreditInfoAccessDangerousApi {

    /** Получение прямого доступа к тексту "Долг". */
    val debtTitle: SbisTextView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к текстовому значению долга. */
    val debtValueText: SbisTextView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}