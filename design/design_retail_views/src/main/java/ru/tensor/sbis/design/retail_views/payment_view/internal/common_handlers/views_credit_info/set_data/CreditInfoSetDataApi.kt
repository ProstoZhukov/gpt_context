package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_credit_info.set_data

import java.math.BigDecimal

/** Обобщение API для установки данных в блок "информация о долге". */
interface CreditInfoSetDataApi {
    /** Установка текстового значения [debtValue] суммы долга. */
    fun setDebtValue(debtValue: BigDecimal)
}