package ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.action_listeners.CashInputActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.action_listeners.ExtraViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.action_listeners.MixPaymentActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.action_listeners.PaymentButtonsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners.PaymentTypeActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners.ToolbarViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseActionListenerApi

/**
 * Класс для объединения Api компонента связанного с установкой
 * слушателей и действий реализующих кастомное поведение.
 */
interface AdvanceActionListenerApi : BaseActionListenerApi {

    /** Объект предоставляющий доступ к API [AdvanceActionListenerApi.Handler]. */
    override val actionListenerApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [AdvanceActionListenerApi]. */
    interface Handler :
        BaseActionListenerApi.Handler,
        MixPaymentActionListenerApi,
        CashInputActionListenerApi,
        ExtraViewsActionListenerApi,
        PaymentTypeActionListenerApi,
        ToolbarViewsActionListenerApi,
        PaymentButtonsActionListenerApi
}