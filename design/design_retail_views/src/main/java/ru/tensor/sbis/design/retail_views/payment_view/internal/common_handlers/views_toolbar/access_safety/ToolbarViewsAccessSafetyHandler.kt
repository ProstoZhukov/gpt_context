package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_safety

import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi

/** Реализация объекта для безопасного доступа к элементам управления в шапке окна оплаты. */
internal class ToolbarViewsAccessSafetyHandler(
    private val viewAccessApi: ToolbarViewsAccessDangerousApi
) : ToolbarViewsAccessSafetyApi {

    override fun setClientButtonVisibility(isVisible: Boolean) {
        viewAccessApi.clientButton.isVisible = isVisible
    }

    override fun setCloseButtonVisibility(isVisible: Boolean) {
        viewAccessApi.closeButton.isVisible = isVisible
    }

    override fun setClientButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.clientButton.isEnabled = isEnabled
    }

    override fun setCloseButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.closeButton.isEnabled = isEnabled
    }
}