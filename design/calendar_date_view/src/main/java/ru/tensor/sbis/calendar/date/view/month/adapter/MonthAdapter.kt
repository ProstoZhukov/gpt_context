package ru.tensor.sbis.calendar.date.view.month.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.view.DayViewHolder
import ru.tensor.sbis.calendar.date.view.EmptySelectedDayDrawable
import ru.tensor.sbis.calendar.date.view.SelectedDayDrawable
import ru.tensor.sbis.calendar.date.view.selector.AdapterNotifier
import ru.tensor.sbis.calendar.date.view.selector.NoSelector
import ru.tensor.sbis.calendar.date.view.selector.Selector
import kotlin.properties.Delegates

internal class MonthAdapter : RecyclerView.Adapter<DayViewHolder>(), AdapterNotifier {
    var selector: Selector = NoSelector()

    var selectedDayDrawable: SelectedDayDrawable = EmptySelectedDayDrawable()

    private val _data = mutableMapOf<LocalDate, Day>()
    var data: Map<LocalDate, Day> = _data
        set(value) {
            _data.putAll(value)
            notifyDataSetChanged()
        }

    var month: LocalDate by Delegates.observable(LocalDate.now()) { _, _, newValue ->
        calendarMonth = newValue.withDayOfMonth(1)
    }

    private var calendarMonth: LocalDate by Delegates.observable(LocalDate.now()) { _, _, newValue ->
        calculate(newValue)
    }

    init {
        calculate(calendarMonth)
    }

    private fun calculate(newValue: LocalDate) {
        offsetDays = newValue.dayOfWeek - 1
        itemsCount = newValue.dayOfMonth().maximumValue + offsetDays
        notifyDataSetChanged()
    }

    private var offsetDays = 0 // Количество пустых дней перед первым днем месяца
    private var itemsCount = 0

    override fun getItemCount() = itemsCount
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder = DayViewHolder(parent)
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val localDate = calendarMonth.plusDays(position - offsetDays)
        when {
            position < offsetDays       -> holder.clear()
            data.containsKey(localDate) -> holder.bind(data[localDate]!!, selector, selectedDayDrawable)
            else                        -> holder.bind(
                Day(
                    date = localDate,
                    type = if (localDate.dayOfWeek > DateTimeConstants.FRIDAY) EventType.DAY_OFF else EventType.WORKDAY
                ),
                selector,
                selectedDayDrawable
            )
        }
    }

}