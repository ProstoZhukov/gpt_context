package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.dangerous

import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_views.payment_view.PaymentView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_dangerous.AllInputFieldsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_dangerous.CommentInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseViewAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.DepositWithdrawalDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalViewAccessSafetyApi

/**
 * Интерфейс для объединения Api компонента связанного с прямым доступом к элементам [DepositWithdrawalDelegate].
 *
 * В случае, если в текущем режиме работы View элемент не может быть найден, будет выброшен
 * exception [ViewNotExistInActivePaymentDelegate].
 *
 * ВАЖНО! Используйте данной API на свой страх и риск, прямой доступ к элементам может привести
 * к непредсказуемым последствиям и сломать работу [DepositWithdrawalDelegate].
 * Предпочтительно использовать [DepositWithdrawalViewAccessSafetyApi].
 */
internal interface DepositWithdrawalViewAccessDangerousApi : BaseViewAccessDangerousApi {

    /** Объект предоставляющий доступ к API [DepositWithdrawalViewAccessDangerousApi.Handler]. */
    override val viewAccessApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DepositWithdrawalViewAccessDangerousApi]. */
    interface Handler :
        BaseViewAccessDangerousApi.Handler,
        CashInputAccessDangerousApi,
        CommentInfoAccessDangerousApi,
        ToolbarViewsAccessDangerousApi,
        AllInputFieldsAccessDangerousApi {

        /** Получение прямого доступа [PaymentView]. */
        val paymentView: PaymentView
            @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

        /** Получение прямого доступа к кнопке "Внести". */
        val depositButton: SbisButton
            @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

        /** Получение прямого доступа к кнопке "Изъять". */
        val withdrawalButton: SbisButton
            @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
    }
}