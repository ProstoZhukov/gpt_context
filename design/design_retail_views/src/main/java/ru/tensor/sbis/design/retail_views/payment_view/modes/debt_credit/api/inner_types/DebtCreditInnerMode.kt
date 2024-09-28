package ru.tensor.sbis.design.retail_views.payment_view.modes.debt_credit.api.inner_types

import ru.tensor.sbis.design.retail_views.R

/** Режимы внутри делегата "Оплата кредита". */
enum class DebtCreditInnerMode(val stringResId: Int) {
    /** Оплата кредита. */
    CREDIT_PAYMENT(R.string.retail_views_credit_payment_title),

    /** Погасить досрочно. */
    FULL_REPAYMENT(R.string.retail_views_credit_full_repayment_title)
}