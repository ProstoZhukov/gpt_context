package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_safety

import androidx.core.view.isVisible
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous.MixPaymentAccessDangerousApi

/** Реализация объекта для безопасного доступа к элементам управления "переключение режима оплаты". */
internal class MixPaymentAccessSafetyHandler(
    private val viewAccessApi: MixPaymentAccessDangerousApi
) : MixPaymentAccessSafetyApi {

    override fun setCurrentMixPaymentMode(isMixedMode: Boolean) {
        setEnableMixButtonVisibility(!isMixedMode)
        setDisableMixButtonVisibility(isMixedMode)
    }

    override fun setEnableMixButtonVisibility(isVisible: Boolean) {
        viewAccessApi.enableMixButton.isVisible = isVisible
    }

    override fun setDisableMixButtonVisibility(isVisible: Boolean) {
        viewAccessApi.disableMixButton.isVisible = isVisible
    }

    override fun setEnableMixButtonEnableState(isEnabled: Boolean) {
        viewAccessApi.enableMixButton.isEnabled = isEnabled
    }

    override fun setDisableMixButtonEnableState(isEnabled: Boolean) {
        viewAccessApi.disableMixButton.isVisible = isEnabled
    }
}