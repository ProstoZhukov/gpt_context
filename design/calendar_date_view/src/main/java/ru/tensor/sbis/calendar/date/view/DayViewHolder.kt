package ru.tensor.sbis.calendar.date.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.calendar.date.view.day.DayView
import ru.tensor.sbis.calendar.date.view.selector.Selector
import androidx.core.view.isInvisible

internal class DayViewHolder(val view: DayView, val now: LocalDate) : RecyclerView.ViewHolder(view) {
    var drawBackgroundUsualDay: Boolean = true

    constructor(parent: ViewGroup) :
            this(LayoutInflater.from(parent.context).inflate(R.layout.month_picker_day, parent, false) as DayView,
            LocalDate.now())
    constructor(dayView: DayView) : this(dayView, LocalDate.now())

    fun clear() {
        view.isUnwantedVacationDay = false
        view.isInvisible = true
        view.setOnClickListener(null)
        view.dateFullText = null
        view.dateText = null
        view.dayBackgroundColor = null
    }

    fun bind(info: Day, selector: Selector, selectedDayDrawable: SelectedDayDrawable) {
        view.isInvisible = false
        view.drawBackgroundUsualDay = drawBackgroundUsualDay

        view.isCurrent = info.date == now
        view.isHoliday = info.isHoliday
        view.dateText = info.date.dayOfMonth.toString()
        view.dateFullText = info.date
        view.coloringScheme = info.dayActivitiesPaintingList
        view.dayType = info.type
        view.dayBackgroundColor = if (info.backgroundColor.isNotBlank()) info.parseBackgroundColor else null
        view.isVacationOnHoliday = info.isVacationOnHoliday
        view.isUnwantedVacationDay = info.isUnwantedVacDay
        view.selectedDayDrawable = selectedDayDrawable
        view.selectedType = selector.getSelectedType(info.date).apply {
            isHoliday = info.isHoliday
        }
        view.setOnClickListener {
            selector.onClick(info.date)
        }
    }
}