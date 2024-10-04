package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.action_listeners

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_toolbar.access_dangerous.ToolbarViewsAccessDangerousApi
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener

/** Общая реализация объекта для обработки действий пользователя с View элементами в шапке окна оплаты. */
internal class ToolbarViewsActionListenerHandler(
    private val viewAccessApi: ToolbarViewsAccessDangerousApi
) : ToolbarViewsActionListenerApi {

    override fun setClientClickListener(action: () -> Unit) {
        viewAccessApi.clientButton.preventDoubleClickListener(LONG_CLICK_DELAY) { action.invoke() }
    }

    override fun setCloseClickListener(action: () -> Unit) {
        viewAccessApi.closeButton.preventDoubleClickListener(LONG_CLICK_DELAY) { action.invoke() }
    }
}