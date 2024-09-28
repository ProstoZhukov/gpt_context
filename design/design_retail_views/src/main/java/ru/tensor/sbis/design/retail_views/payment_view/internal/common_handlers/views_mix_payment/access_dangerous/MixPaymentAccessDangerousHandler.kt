package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_mix_payment.access_dangerous

import android.view.View
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi

/** Реализация объекта для прямого доступа к элементам управления "переключение режима оплаты". */
@DangerousApi
internal class MixPaymentAccessDangerousHandler(
    private val rootDelegateContainer: View
) : MixPaymentAccessDangerousApi {

    override val enableMixButton: SbisRoundButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_enable_mix_payment)

    override val disableMixButton: SbisButton
        get() = rootDelegateContainer.findViewById(R.id.retail_views_disable_mix_payment)
}