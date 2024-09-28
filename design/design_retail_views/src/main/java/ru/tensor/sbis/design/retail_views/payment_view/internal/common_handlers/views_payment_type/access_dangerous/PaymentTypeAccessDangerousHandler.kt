package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Реализация объекта для прямого доступа к View элементам "тип оплаты". */
@DangerousApi
internal class PaymentTypeAccessDangerousHandler(
    private val rootDelegateContainer: View
) : PaymentTypeAccessDangerousApi {

    override val paymentTypeButton: SbisButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_payment_type_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "paymentTypeButton",
                viewResIdName = "R.id.retail_views_payment_type_button"
            )
}