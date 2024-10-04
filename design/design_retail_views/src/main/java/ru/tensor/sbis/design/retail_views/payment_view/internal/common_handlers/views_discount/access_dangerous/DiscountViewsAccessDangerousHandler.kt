package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.bonus_button.BonusButtonView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Реализация объекта для прямого доступа к элементам "блок скидок". */
@DangerousApi
internal class DiscountViewsAccessDangerousHandler(
    private val rootDelegateContainer: View
) : DiscountViewsAccessDangerousApi {

    override val discountButton: SbisButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_discount_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "discountButton",
                viewResIdName = "R.id.retail_views_discount_button"
            )

    override val bonusButton: BonusButtonView
        get() = rootDelegateContainer.findViewById(R.id.retail_views_bonus_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "bonusButton",
                viewResIdName = "R.id.retail_views_bonus_button"
            )
}