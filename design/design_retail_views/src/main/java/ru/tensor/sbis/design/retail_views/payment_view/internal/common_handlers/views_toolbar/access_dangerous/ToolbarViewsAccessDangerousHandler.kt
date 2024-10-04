package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate

/** Реализация объекта для прямого доступа к элементам в шапке окна оплаты. */
@DangerousApi
internal class ToolbarViewsAccessDangerousHandler(
    private val rootDelegateContainer: View
) : ToolbarViewsAccessDangerousApi {

    override val closeButton: SbisRoundButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_close_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "closeButton",
                viewResIdName = "R.id.retail_views_close_button"
            )

    override val clientButton: SbisButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_client_button)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "clientButton",
                viewResIdName = "R.id.retail_views_client_button"
            )
}