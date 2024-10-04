package ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api

import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.MixPaymentInitializeParams
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.AdvanceDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.dangerous.AdvanceViewAccessDangerousApi
import kotlin.properties.Delegates

/** Набор Api, необходимый для инициализации [AdvanceDelegate]. */
data class AdvanceInitializeApi(
    val keyboardHelper: CustomNumericKeyboardHelper,
    val mixPaymentInitializeParams: MixPaymentInitializeParams,
    val actionListenerApi: AdvanceActionListenerApi.Handler,
    val setDataApi: AdvanceSetDataApi.Handler,
    val viewSafetyApi: AdvanceViewAccessSafetyApi.Handler
) : BaseInitializeApi {
    /* [Internal Api] - можно использовать только внутри делегатов оплаты. */
    internal var banknotesApi: BanknotesDelegateApi.Handler by Delegates.notNull()

    internal var viewAccessApi: AdvanceViewAccessDangerousApi.Handler by Delegates.notNull()
}