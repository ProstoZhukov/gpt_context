package ru.tensor.sbis.design.retail_views.payment_view.modes.deposit_withdrawal.api

import ru.tensor.sbis.design.retail_views.databinding.RetailViewsDepositLayoutBinding
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseRenderApi

/** Интерфейс для описания Api отрисовки делегата "Внести/Изъять" */
interface DepositWithdrawalRenderApi : BaseRenderApi<RetailViewsDepositLayoutBinding, DepositWithdrawalInitializeApi> {

    /** Объект предоставляющий доступ к API [DepositWithdrawalRenderApi.Handler]. */
    override val renderApiHandler: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DepositWithdrawalRenderApi]. */
    interface Handler : BaseRenderApi.Handler<RetailViewsDepositLayoutBinding, DepositWithdrawalInitializeApi>
}