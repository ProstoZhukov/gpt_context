package ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data.CreditInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.set_data.DiscountViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data.PaymentButtonsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.set_data.TaxationInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data.ToolbarViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.inner_types.PaymentInnerMode

/** Класс для объединения Api компонента связанного с установкой данных в элементы интерфейса. */
interface PaymentSetDataApi : BaseSetDataApi {

    /** Объект предоставляющий доступ к API [PaymentSetDataApi.Handler]. */
    override val setDataApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [PaymentSetDataApi]. */
    interface Handler :
        BaseSetDataApi.Handler,
        CashInputSetDataApi,
        CreditInfoSetDataApi,
        PaymentTypeSetDataApi,
        CommentInfoSetDataApi,
        ToolbarViewsSetDataApi,
        TaxationInfoSetDataApi,
        DiscountViewsSetDataApi,
        PaymentButtonsSetDataApi,
        PaymentTypeSetDataApi.TypeChanger<PaymentInnerMode> {

        /** Список типов оплат доступных в контекстном меню. */
        var allowedPaymentTypes: List<PaymentInnerMode>
    }
}