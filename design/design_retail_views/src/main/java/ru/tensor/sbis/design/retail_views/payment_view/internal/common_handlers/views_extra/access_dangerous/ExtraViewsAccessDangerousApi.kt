package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous

import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Обобщение API для доступа к элементам "блок дополнительных действий". */
interface ExtraViewsAccessDangerousApi {

    /** Получение прямого доступа к кнопке дополнительных операции. */
    val moreButton: SbisRoundButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к кнопке оплаты по QR-коду. */
    val qrCodeButton: SbisRoundButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к кнопке отправки счета. */
    val sendButton: SbisRoundButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}