package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api

import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.MixPaymentInitializeParams
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.DebtCreditDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.dangerous.DebtCreditViewAccessDangerousApi
import kotlin.properties.Delegates

/** Набор Api, необходимый для инициализации [DebtCreditDelegate]. */
data class DebtCreditInitializeApi(
    val keyboardHelper: CustomNumericKeyboardHelper,
    val mixPaymentInitializeParams: MixPaymentInitializeParams,
    val setDataApi: DebtCreditSetDataApi.Handler,
    val actionListenerApi: DebtCreditActionListenerApi.Handler,
    val viewSafetyApi: DebtCreditViewAccessSafetyApi.Handler
) : BaseInitializeApi {
    /* [Internal Api] - можно использовать только внутри делегатов оплаты. */
    internal var banknotesApi: BanknotesDelegateApi.Handler by Delegates.notNull()
    internal var viewAccessApi: DebtCreditViewAccessDangerousApi.Handler by Delegates.notNull()
}