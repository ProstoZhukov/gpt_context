package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners

/** Обобщение Api для обработки действий пользователя с View элементами в шапке окна оплаты. */
interface ToolbarViewsActionListenerApi {

    /** Установка действия [action] по нажатию на кнопку "Клиент". */
    fun setClientClickListener(action: () -> Unit)

    /** Установка действия [action] по нажатию на кнопку "Закрыть". */
    fun setCloseClickListener(action: () -> Unit)
}