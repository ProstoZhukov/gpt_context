package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api

import ru.tensor.sbis.design.retail_views.numberic_keyboard.helpers.CustomNumericKeyboardHelper
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.BanknotesDelegateApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseInitializeApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.DepositWithdrawalDelegate
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.dangerous.DepositWithdrawalViewAccessDangerousApi
import kotlin.properties.Delegates

/** Набор Api, необходимый для инициализации [DepositWithdrawalDelegate]. */
data class DepositWithdrawalInitializeApi(
    val keyboardHelper: CustomNumericKeyboardHelper
) : BaseInitializeApi {
    /* [Internal Api] - можно использовать только внутри делегатов оплаты. */
    internal var banknotesApi: BanknotesDelegateApi.Handler by Delegates.notNull()

    internal var viewAccessApi: DepositWithdrawalViewAccessDangerousApi.Handler by Delegates.notNull()
}