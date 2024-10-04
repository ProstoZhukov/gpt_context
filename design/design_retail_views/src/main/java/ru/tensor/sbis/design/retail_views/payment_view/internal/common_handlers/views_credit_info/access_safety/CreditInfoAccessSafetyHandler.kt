package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_safety

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous.CreditInfoAccessDangerousApi

/** Реализация объекта для безопасного доступа к View элементам "информация о долге". */
internal class CreditInfoAccessSafetyHandler(
    private val viewAccessApi: CreditInfoAccessDangerousApi
) : CreditInfoAccessSafetyApi {

    override fun setDebtTextVisibility(isVisible: Boolean) {
        viewAccessApi.debtTitle.isVisible = isVisible
        viewAccessApi.debtValueText.isVisible = isVisible
    }

    override fun setDebtTextInvisible(isInvisible: Boolean) {
        viewAccessApi.debtTitle.isInvisible = isInvisible
        viewAccessApi.debtValueText.isInvisible = isInvisible
    }
}