package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Реализация объекта для прямого доступа к элементам "блок дополнительных действий". */
@DangerousApi
internal class ExtraViewsAccessDangerousHandler(
    private val rootDelegateContainer: View
) : ExtraViewsAccessDangerousApi {

    override val moreButton: SbisRoundButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_more_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "moreButton",
                viewResIdName = "R.id.retail_views_more_button"
            )

    override val qrCodeButton: SbisRoundButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_payment_qr_code_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "qrCodeButton",
                viewResIdName = "R.id.retail_views_payment_qr_code_button"
            )

    override val sendButton: SbisRoundButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_payment_send_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "sendButton",
                viewResIdName = "R.id.retail_views_payment_send_button"
            )
}