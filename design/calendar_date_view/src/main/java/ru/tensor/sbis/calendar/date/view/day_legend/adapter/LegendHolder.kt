package ru.tensor.sbis.calendar.date.view.day_legend.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.calendar.date.view.day.LegendDayView
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import ru.tensor.sbis.design.utils.LocaleUtils
import java.text.SimpleDateFormat

/**
 * Абстрактный класс для ViewHolder дня.
 *
 * @author im.zheglov
 */
abstract class DayHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(info: Day, onDayClicked: ((LocalDate) -> Unit)?, showHintMode: Int, eventsCountPosition: Int, showReportMarkForced: Boolean = false)
}

/**
 * ViewHolder для дня в легенде.
 *
 * @author im.zheglov
 */
class DayLegendHolder(
    private val view: LegendDayView
) : DayHolder(view) {
    constructor(parent: ViewGroup) : this(
        LegendDayView(parent.context)
    )

    private val colorsProvider = ColorsProvider(view.context)

    private val colorWorkday = colorsProvider.workdayText
    private val colorHoliday = colorsProvider.colorHolidayText

    private val simpleDateFormat = SimpleDateFormat("EE", LocaleUtils.getDefaultLocale(view.context))

    override fun bind(info: Day, onDayClicked: ((LocalDate) -> Unit)?, showHintMode: Int, eventsCountPosition: Int, showReportMarkForced: Boolean) {
        view.showHintMode = showHintMode
        view.eventsCountPosition = eventsCountPosition
        view.eventsCountText = if (info.payloadEventsCount > 0) info.payloadEventsCount.toString() else ""
        val isReportDay = info.reportsCount != null && info.reportsCount > 0
        val busyLevel = info.busyLevel
        if (isReportDay && showReportMarkForced) busyLevel[0] = true
        view.busyLevel = busyLevel
        view.busyLevelColor =
            if (isReportDay) colorsProvider.colorEventsMarkReport
            else colorsProvider.colorEventsMarkDefault

        view.date = info.date
        view.dateText = info.date.dayOfMonth.toString()
        view.dayOfWeekText = simpleDateFormat.format(info.date.toDate()).toUpperCase()
        view.dayOfWeekTextColor = if (!info.isWorkday || info.isHoliday) colorHoliday else colorWorkday
        view.dateTextColor = if (info.isHoliday) colorHoliday else colorWorkday

        view.coloringScheme = info.dayActivitiesPaintingList
        view.dayType = info.type

        view.isUnwantedVacationDay = info.isUnwantedVacDay
        view.isVacationOnHoliday = info.isVacationOnHoliday
        view.dayBackgroundColor = if (info.sbisBackgroundColor != null) {
            info.sbisBackgroundColor.getColor(itemView.context)
        } else if (info.backgroundColor.isNotBlank()) {
            info.parseBackgroundColor
        } else {
            null
        }
        view.setOnClickListener { onDayClicked?.invoke(info.date) }
    }
}
