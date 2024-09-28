package ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api

import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.MixPaymentInitializeParams
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.PaymentDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.dangerous.PaymentViewAccessDangerousApi
import kotlin.properties.Delegates

/** Набор Api, необходимый для инициализации [PaymentDelegate]. */
data class PaymentInitializeApi(
    val keyboardHelper: CustomNumericKeyboardHelper,
    val mixPaymentInitializeParams: MixPaymentInitializeParams,
    val setDataApi: PaymentSetDataApi.Handler,
    val actionListenerApi: PaymentActionListenerApi.Handler,
    val viewSafetyApi: PaymentViewAccessSafetyApi.Handler
) : BaseInitializeApi {
    /* [Internal Api] - можно использовать только внутри делегатов оплаты. */
    internal var banknotesApi: BanknotesDelegateApi.Handler by Delegates.notNull()

    internal var viewAccessApi: PaymentViewAccessDangerousApi.Handler by Delegates.notNull()
}