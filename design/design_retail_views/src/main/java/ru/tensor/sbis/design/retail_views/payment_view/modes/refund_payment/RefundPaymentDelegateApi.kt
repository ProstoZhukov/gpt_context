package ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment

import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentActionListenerApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentRenderApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentSetDataApi
import ru.tensor.sbis.design.retail_views.payment_view.modes.refund_payment.api.RefundPaymentViewAccessSafetyApi

/** Описание объекта, который обеспечивает работу режима "Возврат". */
interface RefundPaymentDelegateApi :
    RefundPaymentRenderApi,
    RefundPaymentSetDataApi,
    RefundPaymentActionListenerApi,
    RefundPaymentViewAccessSafetyApi
