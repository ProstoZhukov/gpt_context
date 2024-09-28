package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.day.DayView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.listeners.CalendarListener

/**
 * ViewHolder ячейки с днём.
 *
 * @author mb.kruglova
 */
internal class DayViewHolder(
    private val view: DayView,
    private val listener: CalendarListener?,
    private val isDayEnabled: Boolean
) : RecyclerView.ViewHolder(view) {

    /** @SelfDocumented */
    fun bind(model: DayModel) = with(view) {
        date = model.date
        isRangePart = model.isRangePart
        isAvailable = model.isAvailable
        customBackgroundColor = model.customTheme.backgroundColor?.getColor(view.context)
        customDayOfWeekColor = model.customTheme.dayOfWeekColor?.getColor(view.context)
        dayOfMonth = model.dayOfMonth
        dayOfWeek = model.dayOfWeek
        daySelection = model.daySelection
        counter = model.counter
        markerType = model.markerType
        isCurrentDay = model.isCurrent

        setOnClickListener {
            if (isDayEnabled && isAvailable && isRangePart) listener?.onClickItem(model.date, false)
        }
    }

    companion object {

        val ITEM_TYPE = R.id.date_picker_day_item_type_id

        /** @SelfDocumented */
        fun create(
            parent: ViewGroup,
            listener: CalendarListener?,
            isEnabled: Boolean
        ) = DayViewHolder(
            DayView(parent.context).apply {
                id = R.id.date_picker_day_item
            },
            listener,
            isEnabled
        )
    }
}