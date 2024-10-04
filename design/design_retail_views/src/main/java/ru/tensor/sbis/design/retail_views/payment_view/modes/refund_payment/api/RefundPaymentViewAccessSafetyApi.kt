package ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety.MixPaymentAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_safety.PaymentButtonsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_safety.PaymentTypeAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_safety.TaxationInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseViewAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.inner_types.RefundPaymentInnerMode

/**
 * Интерфейс для объединения Api компонента связанного с безопасным
 * доступом к View-параметрам элементов интерфейса.
 */
interface RefundPaymentViewAccessSafetyApi : BaseViewAccessSafetyApi {

    /** Объект предоставляющий доступ к API [RefundPaymentViewAccessSafetyApi.Handler]. */
    override val viewSafetyApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [RefundPaymentViewAccessSafetyApi]. */
    interface Handler :
        BaseViewAccessSafetyApi.Handler,
        CashInputAccessSafetyApi,
        MixPaymentAccessSafetyApi,
        CommentInfoAccessSafetyApi,
        PaymentTypeAccessSafetyApi,
        TaxationInfoAccessSafetyApi,
        ToolbarViewsAccessSafetyApi,
        PaymentButtonsAccessSafetyApi,
        AllInputFieldsAccessSafetyApi {

        /*# region ConfigureViewsApi */
        /** Метод переключает привязку элементов в зависимости от типа возврата. */
        fun configureRefundScreenConstraints(refundPaymentInnerMode: RefundPaymentInnerMode)
        /*# endregion */
    }
}