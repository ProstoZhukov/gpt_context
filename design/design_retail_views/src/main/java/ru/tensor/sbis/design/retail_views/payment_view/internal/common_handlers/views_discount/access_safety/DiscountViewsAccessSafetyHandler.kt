package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_safety

import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_dangerous.DiscountViewsAccessDangerousApi

/** Реализация объекта для безопасного доступа к View элементам "блок скидок". */
internal class DiscountViewsAccessSafetyHandler(
    private val viewAccessApi: DiscountViewsAccessDangerousApi
) : DiscountViewsAccessSafetyApi {

    override fun setBonusButtonVisibility(isVisible: Boolean) {
        viewAccessApi.bonusButton.isVisible = isVisible
    }

    override fun setDiscountButtonVisibility(isVisible: Boolean) {
        viewAccessApi.discountButton.isVisible = isVisible
    }

    override fun setBonusButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.bonusButton.isEnabled = isEnabled
    }

    override fun setDiscountButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.discountButton.isEnabled = isEnabled
    }
}