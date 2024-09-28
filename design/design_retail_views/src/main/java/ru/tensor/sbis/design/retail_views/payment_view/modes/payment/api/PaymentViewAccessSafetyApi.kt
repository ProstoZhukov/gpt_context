package ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_all_input.access_safety.AllInputFieldsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_cash_input.access_safety.CashInputAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_comment.access_safety.CommentInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety.CreditInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_discount.access_safety.DiscountViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_safety.ExtraViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety.MixPaymentAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_safety.PaymentButtonsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_safety.PaymentTypeAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_safety.TaxationInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety.ToolbarViewsAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseViewAccessSafetyApi

/**
 * Интерфейс для объединения Api компонента связанного с безопасным
 * доступом к View-параметрам элементов интерфейса.
 * */
interface PaymentViewAccessSafetyApi : BaseViewAccessSafetyApi {

    /** Объект предоставляющий доступ к API [PaymentViewAccessSafetyApi.Handler]. */
    override val viewSafetyApi: Handler

    /** Интерфейс объекта предоставляющего доступ к API [PaymentViewAccessSafetyApi]. */
    interface Handler :
        BaseViewAccessSafetyApi.Handler,
        CashInputAccessSafetyApi,
        ExtraViewsAccessSafetyApi,
        CreditInfoAccessSafetyApi,
        MixPaymentAccessSafetyApi,
        CommentInfoAccessSafetyApi,
        PaymentTypeAccessSafetyApi,
        ToolbarViewsAccessSafetyApi,
        TaxationInfoAccessSafetyApi,
        DiscountViewsAccessSafetyApi,
        PaymentButtonsAccessSafetyApi,
        AllInputFieldsAccessSafetyApi {

        /*# region EnableApi */
        /** Метод для блокировки/разблокировки ручного ввода суммы. */
        fun setManualInputEnable(isEnabled: Boolean)
        /*# endregion */

        /*# region ConfigureViewsApi */
        /** Нужно-ли переопределять хинты значениями по-умолчанию. */
        fun overrideHints(override: Boolean)
        /*# endregion */
    }
}