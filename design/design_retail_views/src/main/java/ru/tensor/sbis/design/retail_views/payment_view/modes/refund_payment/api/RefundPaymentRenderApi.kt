package ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api

import ru.tensor.sbis.design.retail_views.databinding.RetailViewsRefundPaymentLayoutBinding
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseRenderApi

/** Интерфейс для описания Api отрисовки делегата "Возврат" */
interface RefundPaymentRenderApi : BaseRenderApi<RetailViewsRefundPaymentLayoutBinding, RefundPaymentInitializeApi> {

    /** Объект предоставляющий доступ к API [RefundPaymentRenderApi.Handler]. */
    override val renderApiHandler: Handler

    /** Интерфейс объекта предоставляющего доступ к API [RefundPaymentRenderApi]. */
    interface Handler : BaseRenderApi.Handler<RetailViewsRefundPaymentLayoutBinding, RefundPaymentInitializeApi>
}