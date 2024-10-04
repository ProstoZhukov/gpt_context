package ru.tensor.sbis.design.retail_views.payment_view.modes.payment

import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.PaymentViewAccessSafetyApi

/** Описание объекта, который обеспечивает работу режима "Оплата". */
interface PaymentDelegateApi :
    PaymentRenderApi,
    PaymentSetDataApi,
    PaymentActionListenerApi,
    PaymentViewAccessSafetyApi
