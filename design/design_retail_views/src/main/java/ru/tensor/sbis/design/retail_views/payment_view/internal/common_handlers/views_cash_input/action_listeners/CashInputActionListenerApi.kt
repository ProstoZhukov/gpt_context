package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners

import ru.tensor.sbis.design.retail_models.Amount

/** Обобщение Api для обработки действий пользователя с View элементами - "Ввод денежных средств". */
interface CashInputActionListenerApi {
    /** Установка слушателя на изменение поля "Наличными". */
    fun setOnAmountChangedAction(onAmountChangedAction: (Amount) -> Unit)
}