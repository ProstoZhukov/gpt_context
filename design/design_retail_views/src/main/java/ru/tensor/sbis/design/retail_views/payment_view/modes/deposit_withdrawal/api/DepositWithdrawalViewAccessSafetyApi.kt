package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseViewAccessSafetyApi

/**
 * Интерфейс для объединения Api компонента связанного с безопасным
 * доступом к View-параметрам элементов интерфейса.
 */
interface DepositWithdrawalViewAccessSafetyApi : BaseViewAccessSafetyApi {

    /** Объект предоставляющий доступ к API [DepositWithdrawalViewAccessSafetyApi.Handler]. */
    override val viewSafetyApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DepositWithdrawalViewAccessSafetyApi]. */
    interface Handler :
        BaseViewAccessSafetyApi.Handler,
        CashInputAccessSafetyApi,
        CommentInfoAccessSafetyApi,
        ToolbarViewsAccessSafetyApi,
        AllInputFieldsAccessSafetyApi {

        /*# region VisibilityApi */
        /** Установка состояния видимости для кнопки 'Внести'. */
        fun setDepositButtonVisible(isVisible: Boolean)

        /** Установка состояния видимости для кнопки 'Изъять'. */
        fun setWithdrawalButtonVisible(isVisible: Boolean)
        /*# endregion */
    }
}