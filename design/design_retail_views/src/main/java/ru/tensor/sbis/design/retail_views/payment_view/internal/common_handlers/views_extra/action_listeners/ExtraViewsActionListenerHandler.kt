package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.action_listeners

import android.view.View
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous.ExtraViewsAccessDangerousApi
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener

/** Общая реализация объекта для обработки действий пользователя с View элементами - "блок дополнительных действий". */
internal class ExtraViewsActionListenerHandler(
    private val viewAccessApi: ExtraViewsAccessDangerousApi
) : ExtraViewsActionListenerApi {

    override fun setQrCodeClickListener(action: () -> Unit) {
        viewAccessApi.qrCodeButton.preventDoubleClickListener(LONG_CLICK_DELAY) { action.invoke() }
    }

    override fun setMoreClickListener(action: (View) -> Unit) {
        viewAccessApi.moreButton.preventDoubleClickListener { action.invoke(it) }
    }

    override fun setSendClickListener(action: () -> Unit) {
        viewAccessApi.sendButton.preventDoubleClickListener(LONG_CLICK_DELAY) { action.invoke() }
    }
}