package ru.tensor.sbis.hallscheme.v2.business

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.design.R as RDesign

/**
 * Статус брони.
 * @author aa.gulevskiy
 */
enum class BookingStatus(
    internal val value: Int,
    @StringRes internal val iconRes: Int,
    @ColorRes internal val colorRes: Int
) {
    /** Неподтверждённая бронь. */
    UNCONFIRMED_BOOKING(10, RDesign.string.design_mobile_icon_question_new, R.color.hall_scheme_grey),
    /** Подтверждённая бронь. */
    CONFIRMED_BOOKING(20, RDesign.string.design_mobile_icon_time, R.color.hall_scheme_table_confirmed_booking_color),
    /** Гость опаздывает. */
    LATENESS(30, RDesign.string.design_mobile_icon_time, R.color.hall_scheme_table_lateness_color),
    /** Отменённая бронь. */
    CANCELLED_BOOKING(40, RDesign.string.design_mobile_icon_decline, R.color.hall_scheme_grey),
    /** Гость пришёл. */
    GUEST_COME_IN(45, RDesign.string.design_mobile_icon_time, R.color.hall_scheme_green),
    /** Гость не пришёл. */
    GUEST_DID_NOT_COME(50, RDesign.string.design_mobile_icon_not_come, R.color.hall_scheme_grey);
}