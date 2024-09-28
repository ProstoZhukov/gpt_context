package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_dangerous

import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_views.bonus_button.BonusButtonView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Обобщение API для доступа к элементам "блок скидок". */
interface DiscountViewsAccessDangerousApi {

    /** Получение прямого доступа к кнопке "Скидка". */
    val discountButton: SbisButton
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к кнопке "Бонусы". */
    val bonusButton: BonusButtonView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}