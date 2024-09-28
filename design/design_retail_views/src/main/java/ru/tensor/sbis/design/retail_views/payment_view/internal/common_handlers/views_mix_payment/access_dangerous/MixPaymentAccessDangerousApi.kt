package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous

import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Обобщение API для доступа к элементам управления "переключение режима оплаты". */
interface MixPaymentAccessDangerousApi {

    /** Получение прямого доступа к кнопке включить смешанную оплату. */
    val enableMixButton: SbisRoundButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к кнопке выключить смешанную оплату. */
    val disableMixButton: SbisButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}