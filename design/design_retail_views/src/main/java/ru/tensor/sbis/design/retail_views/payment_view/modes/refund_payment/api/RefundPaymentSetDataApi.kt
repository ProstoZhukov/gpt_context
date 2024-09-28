package ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.set_data.CashInputSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.set_data.CommentInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data.PaymentButtonsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data.PaymentTypeSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.set_data.TaxationInfoSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.set_data.ToolbarViewsSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.inner_types.RefundPaymentInnerMode

/** Класс для объединения Api компонента связанного с установкой данных в элементы интерфейса. */
interface RefundPaymentSetDataApi : BaseSetDataApi {

    /** Объект предоставляющий доступ к API [RefundPaymentSetDataApi.Handler]. */
    override val setDataApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [RefundPaymentSetDataApi]. */
    interface Handler :
        BaseSetDataApi.Handler,
        CashInputSetDataApi,
        CommentInfoSetDataApi,
        PaymentTypeSetDataApi,
        ToolbarViewsSetDataApi,
        TaxationInfoSetDataApi,
        PaymentButtonsSetDataApi,
        PaymentTypeSetDataApi.TypeChanger<RefundPaymentInnerMode>
}