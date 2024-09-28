package ru.tensor.sbis.design.moneyview

import androidx.annotation.StringRes

/**
 * Единица измерения.
 */
enum class MoneyUnit(@StringRes val humanName: Int) {
    NONE(R.string.design_moneyview_suffix_none),
    THOUSAND(R.string.design_moneyview_suffix_thousand),
    MILLION(R.string.design_moneyview_suffix_million),
    BILLION(R.string.design_moneyview_suffix_billion) // миллиард
}