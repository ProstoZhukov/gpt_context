package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_safety

import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_dangerous.TaxationInfoAccessDangerousApi

/** Реализация объекта для безопасного доступа к View элементам "блок СНО". */
internal class TaxationInfoAccessSafetyHandler(
    private val viewAccessApi: TaxationInfoAccessDangerousApi
) : TaxationInfoAccessSafetyApi {

    override fun setTaxationSystemInfoVisibility(isVisible: Boolean) {
        viewAccessApi.taxationContentRootContainer.isVisible = isVisible
    }

    override fun setTaxationSystemInfoEnabled(isEnabled: Boolean) {
        viewAccessApi.taxationContentRootContainer.isEnabled = isEnabled
    }
}