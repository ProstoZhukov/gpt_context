package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal

import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api.DepositWithdrawalViewAccessSafetyApi

/** Описание объекта, который обеспечивает работу режима "Внести/Изъять". */
interface DepositWithdrawalDelegateApi :
    DepositWithdrawalRenderApi,
    DepositWithdrawalSetDataApi,
    DepositWithdrawalActionListenerApi,
    DepositWithdrawalViewAccessSafetyApi