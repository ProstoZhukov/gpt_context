package ru.tensor.sbis.design.retail_views.payment_view.modes.payment.api.inner_types

import ru.tensor.sbis.design.retail_views.R

/** Перечисление вариантов в меню типа оплаты. */
enum class PaymentInnerMode(val stringResId: Int) {
    /** Расчет. */
    PAYMENT(R.string.retail_views_payment_type_payment_text),

    /** Предоплата. */
    PREPAYMENT(R.string.retail_views_payment_type_prepayment_text),

    /** Кредит. */
    CREDIT(R.string.retail_views_payment_type_credit_text),

    /** Нефискальный. */
    NONFISCAL(R.string.retail_views_payment_type_non_fiscal)
}