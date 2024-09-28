package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners

import ru.tensor.sbis.design.retail_models.Amount
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi

/** Общая реализация объекта для обработки действий пользователя с View элементами - "Ввод денежных средств". */
internal class CashInputActionListenerHandler(
    private val banknotesApi: BanknotesDelegateApi.Handler
) : CashInputActionListenerApi {

    override fun setOnAmountChangedAction(onAmountChangedAction: (Amount) -> Unit) {
        /* Устанавливаем слушатель на изменение кол-ва внесенных средств. */
        banknotesApi.subscribeToAmountChangedAction(onAmountChangedAction)
    }
}