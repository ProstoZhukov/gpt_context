package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api

import ru.tensor.sbis.design.retail_views.databinding.RetailViewsDebtCreditLayoutBinding
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseRenderApi

/** Интерфейс для описания Api отрисовки делегата "Оплата кредита (Долги)" */
interface DebtCreditRenderApi : BaseRenderApi<RetailViewsDebtCreditLayoutBinding, DebtCreditInitializeApi> {

    /** Объект предоставляющий доступ к API [DebtCreditRenderApi.Handler]. */
    override val renderApiHandler: Handler

    /** Интерфейс объекта предоставляющего доступ к API [DebtCreditRenderApi]. */
    interface Handler : BaseRenderApi.Handler<RetailViewsDebtCreditLayoutBinding, DebtCreditInitializeApi>
}