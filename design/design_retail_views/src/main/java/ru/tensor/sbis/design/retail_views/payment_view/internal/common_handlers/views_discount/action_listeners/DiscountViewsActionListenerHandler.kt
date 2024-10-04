package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.action_listeners

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_dangerous.DiscountViewsAccessDangerousApi
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener
import java.math.BigDecimal

/** Общая реализация объекта для обработки действий пользователя с View элементами - "блок скидок". */
internal class DiscountViewsActionListenerHandler(
    private val viewAccessApi: DiscountViewsAccessDangerousApi
) : DiscountViewsActionListenerApi {

    override var onBonusesActivatedListener: ((BigDecimal) -> Boolean)? = null
        set(value) {
            field = value

            /* Устанавливаем слушатель на кнопку бонусов. */
            viewAccessApi.bonusButton.setOnActivatedListener { field?.invoke(it) }
        }

    override var onBonusesDeactivatedListener: (() -> Unit)? = null
        set(value) {
            field = value

            /* Устанавливаем слушатель на кнопку бонусов. */
            viewAccessApi.bonusButton.setOnDeactivatedListener { field?.invoke() }
        }

    override fun setDiscountClickListener(action: () -> Unit) {
        viewAccessApi.discountButton.preventDoubleClickListener { action.invoke() }
    }
}