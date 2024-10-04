package ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api

import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.MixPaymentInitializeParams
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.RefundPaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.dangerous.RefundPaymentViewAccessDangerousApi
import kotlin.properties.Delegates

/** Набор Api, необходимый для инициализации [RefundPaymentDelegate]. */
data class RefundPaymentInitializeApi(
    val keyboardHelper: CustomNumericKeyboardHelper,
    val mixPaymentInitializeParams: MixPaymentInitializeParams,
    val setDataApi: RefundPaymentSetDataApi.Handler,
    val actionListenerApi: RefundPaymentActionListenerApi.Handler,
    val viewSafetyApi: RefundPaymentViewAccessSafetyApi.Handler
) : BaseInitializeApi {
    /* [Internal Api] - можно использовать только внутри делегатов оплаты. */
    internal var banknotesApi: BanknotesDelegateApi.Handler by Delegates.notNull()

    internal var viewAccessApi: RefundPaymentViewAccessDangerousApi.Handler by Delegates.notNull()
}