package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.action_listeners

import java.math.BigDecimal

/** Обобщение Api для обработки действий пользователя с View элементами - "кнопки оплаты". */
interface PaymentButtonsActionListenerApi {
    /** Установка действия [action] по изменению суммы в двойной кнопке оплаты. */
    fun setDoubleButtonCheckAmountChangedListener(action: (checkAmount: BigDecimal) -> Unit)

    /** Установка действия [action] по нажатию на кнопку оплаты. */
    fun setCheckClickListener(action: (mixPaymentMode: Boolean) -> Unit)

    /** Установка действия [action] по нажатию на кнопку "Оплатить картой". */
    fun setCardPaymentClickListener(action: () -> Unit)
}