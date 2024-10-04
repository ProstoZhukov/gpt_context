package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils

import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.utils.quarter1Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter2Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter3Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter4Range
import java.util.Calendar.*

/**@SelfDocumented*/
internal fun mapMonthToMonthResId(month: Int): Int {
    return when (month) {
        JANUARY -> R.id.january_title
        FEBRUARY -> R.id.february_title
        MARCH -> R.id.march_title
        APRIL -> R.id.april_title
        MAY -> R.id.may_title
        JUNE -> R.id.june_title
        JULY -> R.id.july_title
        AUGUST -> R.id.august_title
        SEPTEMBER -> R.id.september_title
        OCTOBER -> R.id.october_title
        NOVEMBER -> R.id.november_title
        DECEMBER -> R.id.december_title
        else -> throw IllegalArgumentException()
    }
}

/**@SelfDocumented*/
internal fun mapMonthToQuarterResId(month: Int): Int {
    return when (month) {
        in quarter1Range -> R.id.quarter_1_title
        in quarter2Range -> R.id.quarter_2_title
        in quarter3Range -> R.id.quarter_3_title
        in quarter4Range -> R.id.quarter_4_title
        else -> throw IllegalArgumentException()
    }
}

/**@SelfDocumented*/
internal fun mapMonthToHalfYearResId(month: Int): Int {
    return when {
        month <= JUNE -> R.id.half_year_1_title
        month <= DECEMBER -> R.id.half_year_2_title
        else -> throw IllegalArgumentException()
    }
}