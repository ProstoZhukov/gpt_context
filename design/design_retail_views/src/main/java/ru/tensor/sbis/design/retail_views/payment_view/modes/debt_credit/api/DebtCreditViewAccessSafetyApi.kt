package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety.CreditInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_safety.ExtraViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety.MixPaymentAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_safety.PaymentButtonsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseViewAccessSafetyApi

/**
 * Интерфейс для объединения Api компонента связанного с безопасным
 * доступом к View-параметрам элементов интерфейса.
 */
interface DebtCreditViewAccessSafetyApi : BaseViewAccessSafetyApi {

    /** Объект предоставляющий доступ к API [DebtCreditViewAccessSafetyApi.Handler]. */
    override val viewSafetyApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DebtCreditViewAccessSafetyApi]. */
    interface Handler :
        BaseViewAccessSafetyApi.Handler,
        CashInputAccessSafetyApi,
        CreditInfoAccessSafetyApi,
        ExtraViewsAccessSafetyApi,
        MixPaymentAccessSafetyApi,
        ToolbarViewsAccessSafetyApi,
        PaymentButtonsAccessSafetyApi,
        AllInputFieldsAccessSafetyApi
}