package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_buttons.set_data

import java.math.BigDecimal

/** Обобщение API для установки данных в блок "кнопки оплаты". */
interface PaymentButtonsSetDataApi {

    /** Установка текстового значения [amount] суммы оплаты/внесения. */
    fun setCheckButtonTextValue(amount: BigDecimal)

    /** Установка текстового значения [amount] суммы оплаты/внесения в двойную кнопку. */
    fun setCheckDoubleButtonTextValue(amount: BigDecimal)

    /** Установка текстового значения [amount] в поле "оплата картой". */
    fun setPayCardValueTextValue(amount: BigDecimal)

    /** Метод для сброса значения в поле "оплата картой". */
    fun dropPayCardValue()
}