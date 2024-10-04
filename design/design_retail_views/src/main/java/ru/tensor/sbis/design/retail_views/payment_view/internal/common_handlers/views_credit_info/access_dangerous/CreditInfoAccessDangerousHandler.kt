package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous

import android.view.View
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Реализация объекта для прямого доступа к View элементам "информация о долге". */
@DangerousApi
internal class CreditInfoAccessDangerousHandler(
    private val rootDelegateContainer: View
) : CreditInfoAccessDangerousApi {

    override val debtTitle: SbisTextView
        get() = rootDelegateContainer.findViewById(R.id.retail_views_txt_debt)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "debtTitle",
                viewResIdName = "R.id.retail_views_txt_debt"
            )

    override val debtValueText: SbisTextView
        get() = rootDelegateContainer.findViewById(R.id.retail_views_txt_debt_value)
            ?: throw ViewNotExistInActivePaymentDelegate(
                viewName = "debtValueText",
                viewResIdName = "R.id.retail_views_txt_debt_value"
            )
}