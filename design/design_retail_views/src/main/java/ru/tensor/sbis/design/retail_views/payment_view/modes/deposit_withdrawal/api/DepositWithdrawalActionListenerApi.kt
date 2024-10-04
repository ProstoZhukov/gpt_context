package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners.CommentInfoActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners.ToolbarViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseActionListenerApi

/**
 * Класс для объединения Api компонента связанного с установкой
 * слушателей и действий реализующих кастомное поведение.
 */
interface DepositWithdrawalActionListenerApi : BaseActionListenerApi {

    /** Объект предоставляющий доступ к API [DepositWithdrawalActionListenerApi.Handler]. */
    override val actionListenerApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DepositWithdrawalActionListenerApi]. */
    interface Handler :
        BaseActionListenerApi.Handler,
        CashInputActionListenerApi,
        CommentInfoActionListenerApi,
        ToolbarViewsActionListenerApi {

        /** Установка действия [action] по нажатию на кнопку "Внести". */
        fun setDepositClickListener(action: () -> Unit)

        /** Установка действия [action] по нажатию на кнопку "Изъять". */
        fun setWithdrawalClickListener(action: () -> Unit)
    }
}