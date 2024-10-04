package ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api

import ru.tensor.sbis.design.retail_views.databinding.RetailViewsPaymentLayoutBinding
import ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api.BaseRenderApi

/** Интерфейс для описания Api отрисовки делегата "Оплата" */
interface PaymentRenderApi : BaseRenderApi<RetailViewsPaymentLayoutBinding, PaymentInitializeApi> {

    /** Объект предоставляющий доступ к API [PaymentRenderApi.Handler]. */
    override val renderApiHandler: Handler

    /** Интерфейс объекта предоставляющего доступ к API [PaymentRenderApi]. */
    interface Handler : BaseRenderApi.Handler<RetailViewsPaymentLayoutBinding, PaymentInitializeApi>
}