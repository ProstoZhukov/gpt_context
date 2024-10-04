package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.double_button.DoubleButtonApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Реализация объекта для прямого доступа к View элементам "кнопки оплаты". */
@DangerousApi
internal class PaymentButtonsAccessDangerousHandler(
    private val rootDelegateContainer: View
) : PaymentButtonsAccessDangerousApi {

    override val checkButton: SbisButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_check_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "checkButton",
                viewResIdName = "R.id.retail_views_check_button"
            )

    override val checkDoubleButton: DoubleButtonApi
        get() = rootDelegateContainer.findViewById(R.id.retail_views_check_double_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "checkDoubleButton",
                viewResIdName = "R.id.retail_views_check_double_button"
            )

    override val payCardButton: DoubleButtonApi
        get() = rootDelegateContainer.findViewById(R.id.retail_views_card_payment_double_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "payCardButton",
                viewResIdName = "R.id.retail_views_card_payment_double_button"
            )

    override val dummyPaymentButtonGuidelinePortLayoutView: View?
        get() = rootDelegateContainer.findViewById(R.id.retail_views_guideline_keyboard_width)
}