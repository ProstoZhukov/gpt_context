package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners

import android.view.View

/** Обобщение Api для обработки действий пользователя с View элементами - "тип оплаты". */
interface PaymentTypeActionListenerApi {
    /** Установить действие [action] по нажатию на кнопку "тип оплаты". */
    fun setPaymentTypeListener(action: (view: View) -> Unit)
}