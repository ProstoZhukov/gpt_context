package ru.tensor.sbis.design.retail_views.payment_view.modes.advance.api.inner_types

import ru.tensor.sbis.design.retail_views.R

/** Режимы внутри делегата "аванс". */
enum class AdvancePaymentInnerMode(val stringResId: Int) {
    /** Возврат (режим "Аванс"). */
    ADVANCE(R.string.retail_views_payment_type_advance_text)
}