package ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.action_listeners.CommentInfoActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.action_listeners.DiscountViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.action_listeners.ExtraViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.action_listeners.MixPaymentActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.action_listeners.PaymentButtonsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners.PaymentTypeActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.action_listeners.TaxationInfoActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners.ToolbarViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.inner_types.PaymentInnerMode

/**
 * Класс для объединения Api компонента связанного с установкой слушателей и
 * действий реализующих кастомное поведение.
 * */
interface PaymentActionListenerApi : BaseActionListenerApi {

    /** Объект предоставляющий доступ к API [PaymentActionListenerApi.Handler]. */
    override val actionListenerApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [PaymentActionListenerApi]. */
    interface Handler :
        BaseActionListenerApi.Handler,
        CashInputActionListenerApi,
        MixPaymentActionListenerApi,
        ExtraViewsActionListenerApi,
        CommentInfoActionListenerApi,
        PaymentTypeActionListenerApi,
        TaxationInfoActionListenerApi,
        ToolbarViewsActionListenerApi,
        DiscountViewsActionListenerApi,
        PaymentButtonsActionListenerApi {

        /** Слушатель выбора типа оплаты. */
        var onPaymentTypeClick: ((PaymentInnerMode) -> Unit)?

    }
}