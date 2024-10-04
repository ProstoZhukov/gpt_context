package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.dangerous

import ru.tensor.sbis.design.retail_views.payment_view.PaymentView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_dangerous.AllInputFieldsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_dangerous.CashInputAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous.CreditInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous.ExtraViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous.MixPaymentAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous.PaymentButtonsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous.PaymentTypeAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseViewAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.DebtCreditDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.DebtCreditViewAccessSafetyApi

/**
 * Интерфейс для объединения Api компонента связанного с прямым доступом к элементам [DebtCreditDelegate].
 *
 * В случае, если в текущем режиме работы View элемент не может быть найден, будет выброшен
 * exception [ViewNotExistInActivePaymentDelegate].
 *
 * ВАЖНО! Используйте данной API на свой страх и риск, прямой доступ к элементам может привести
 * к непредсказуемым последствиям и сломать работу [DebtCreditDelegate].
 * Предпочтительно использовать [DebtCreditViewAccessSafetyApi].
 */
internal interface DebtCreditViewAccessDangerousApi : BaseViewAccessDangerousApi {

    /** Объект предоставляющий доступ к API [DebtCreditViewAccessDangerousApi.Handler]. */
    override val viewAccessApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DebtCreditViewAccessDangerousApi]. */
    interface Handler :
        BaseViewAccessDangerousApi.Handler,
        CashInputAccessDangerousApi,
        MixPaymentAccessDangerousApi,
        ExtraViewsAccessDangerousApi,
        CreditInfoAccessDangerousApi,
        PaymentTypeAccessDangerousApi,
        ToolbarViewsAccessDangerousApi,
        PaymentButtonsAccessDangerousApi,
        AllInputFieldsAccessDangerousApi {

        /** Получение прямого доступа [PaymentView]. */
        val paymentView: PaymentView
            @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
    }
}