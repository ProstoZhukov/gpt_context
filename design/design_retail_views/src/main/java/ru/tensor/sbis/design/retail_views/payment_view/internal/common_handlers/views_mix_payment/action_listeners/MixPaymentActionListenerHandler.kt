package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.action_listeners

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous.MixPaymentAccessDangerousApi
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener

/** Общая реализация объекта для обработки действий пользователя с View элементами - "переключение режима оплаты". */
internal class MixPaymentActionListenerHandler(
    private val viewAccessApi: MixPaymentAccessDangerousApi,
    initialOnMixPaymentClickExtraAction: ((mixPaymentMode: Boolean) -> Unit)? = null
) : MixPaymentActionListenerApi {

    override var onMixPaymentClickExtraAction: ((mixPaymentMode: Boolean) -> Unit)? =
        initialOnMixPaymentClickExtraAction

    override fun setEnableMixButtonClickListener(action: () -> Unit) {
        viewAccessApi.enableMixButton.preventDoubleClickListener { action() }
    }

    override fun setDisableMixButtonClickListener(action: () -> Unit) {
        viewAccessApi.disableMixButton.preventDoubleClickListener { action() }
    }
}