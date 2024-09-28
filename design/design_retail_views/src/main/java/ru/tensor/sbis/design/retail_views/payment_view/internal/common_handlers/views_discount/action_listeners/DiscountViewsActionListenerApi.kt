package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.action_listeners

import java.math.BigDecimal

/** Обобщение Api для обработки действий пользователя с View элементами - "блок скидок"". */
interface DiscountViewsActionListenerApi {

    /** Поле для установки/получения дополнительного действия, которое выполнится при активации режима бонусов. */
    var onBonusesActivatedListener: ((BigDecimal) -> Boolean)?

    /** Поле для установки/получения дополнительного действия, которое выполнится при деактивации режима бонусов. */
    var onBonusesDeactivatedListener: (() -> Unit)?

    /** Установка действия [action] по нажатию на кнопку "Скидка". */
    fun setDiscountClickListener(action: () -> Unit)
}