package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous

import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Обобщение API для доступа к View элементам "комментарий к оплате". */
interface CommentInfoAccessDangerousApi {

    /** Получение прямого доступа к кнопке "Комментарий". */
    val commentButton: SbisRoundButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к полю "Комментарий". */
    val commentTextView: SbisTextView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}