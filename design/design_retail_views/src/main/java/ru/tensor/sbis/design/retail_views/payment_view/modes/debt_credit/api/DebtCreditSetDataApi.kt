package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data.CreditInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data.PaymentButtonsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data.ToolbarViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.inner_types.DebtCreditInnerMode

/** Класс для объединения Api компонента связанного с установкой данных в элементы интерфейса. */
interface DebtCreditSetDataApi : BaseSetDataApi {

    /** Объект предоставляющий доступ к API [DebtCreditSetDataApi.Handler]. */
    override val setDataApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DebtCreditSetDataApi]. */
    interface Handler :
        BaseSetDataApi.Handler,
        CashInputSetDataApi,
        CreditInfoSetDataApi,
        PaymentTypeSetDataApi,
        ToolbarViewsSetDataApi,
        PaymentButtonsSetDataApi,
        PaymentTypeSetDataApi.TypeChanger<DebtCreditInnerMode> {

        /** Установка типа оплаты кредита. */
        fun setCreditPaymentFullRepayment(isFullRepayment: Boolean)
    }
}