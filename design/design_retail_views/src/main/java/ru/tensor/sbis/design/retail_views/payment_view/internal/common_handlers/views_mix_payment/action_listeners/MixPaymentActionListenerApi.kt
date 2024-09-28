package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.action_listeners

/** Обобщение Api для обработки действий пользователя с View элементами - "переключение режима оплаты". */
interface MixPaymentActionListenerApi {
    /** Установка/получение слушателя дополнительного действия при переключении режимов оплаты. */
    var onMixPaymentClickExtraAction: ((mixPaymentMode: Boolean) -> Unit)?

    /** Установить действие [action] по нажатию на кнопку "Включить смешанную оплату". */
    fun setEnableMixButtonClickListener(action: () -> Unit)

    /** Установить действие [action] по нажатию на кнопку "Выключить смешанную оплату". */
    fun setDisableMixButtonClickListener(action: () -> Unit)
}