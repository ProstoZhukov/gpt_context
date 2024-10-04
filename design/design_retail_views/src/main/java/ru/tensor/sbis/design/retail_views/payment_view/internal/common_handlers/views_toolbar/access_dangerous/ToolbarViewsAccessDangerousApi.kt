package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous

import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Обобщение API для доступа к элементам в шапке окна оплаты. */
interface ToolbarViewsAccessDangerousApi {

    /** Получение прямого доступа к кнопке "Закрыть". */
    val closeButton: SbisRoundButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к кнопке "Клиент". */
    val clientButton: SbisButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

}