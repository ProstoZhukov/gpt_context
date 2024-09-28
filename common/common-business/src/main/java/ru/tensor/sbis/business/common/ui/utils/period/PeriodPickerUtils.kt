package ru.tensor.sbis.business.common.ui.utils.period

import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import java.util.*

/**
 * Преобразует [Period] в [DatePeriod]
 *
 * @return значение [DatePeriod], соответствующее вызывающему объекту
 */
fun Period.toDatePeriod() : DatePeriod {
    return DatePeriod.createWithTimeZoneConversion(dateFrom ?: Calendar.getInstance(),
            dateTo ?: Calendar.getInstance())
}

/**
 * Преобразует [SbisPeriodPickerRange] в [DatePeriod]
 *
 * @return значение [DatePeriod], соответствующее вызывающему объекту
 */
fun SbisPeriodPickerRange.toDatePeriod() : DatePeriod {
    return DatePeriod.createWithTimeZoneConversion(start, end)
}

/**
 * Преобразует [DatePeriod] в [Period]
 *
 * @return значение [Period], соответствующее вызывающему объекту
 */
fun DatePeriod.toPeriod() = Period(toSamePeriodInDefaultTimeZone().calendarFrom,
        toSamePeriodInDefaultTimeZone().calendarTo)