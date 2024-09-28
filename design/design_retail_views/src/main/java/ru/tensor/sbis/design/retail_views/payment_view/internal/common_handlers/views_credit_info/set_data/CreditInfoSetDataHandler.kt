package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data

import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.access_dangerous.CreditInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.utils.intAmountFormat
import java.math.BigDecimal

/** Реализация объекта для установки данных в блок "информация о долге". */
internal class CreditInfoSetDataHandler(
    private val viewAccessApi: CreditInfoAccessDangerousApi
) : CreditInfoSetDataApi {

    override fun setDebtValue(debtValue: BigDecimal) {
        viewAccessApi.debtValueText.text = intAmountFormat.format(debtValue)
    }
}