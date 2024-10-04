package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month

import android.view.View
import com.arkivanov.mvikotlin.core.view.BaseMviView
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerMonthModeFragmentBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.MonthModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.CalendarLayoutManager
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayItemModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView.Model
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.SpanSizeProvider
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.listeners.CalendarListener
import java.util.Calendar

/**
 * Обертка над [View] фрагмента компактного выбора периода.
 *
 * @author mb.kruglova
 */
internal class MonthModePeriodPickerViewImpl(
    private val binding: PeriodPickerMonthModeFragmentBinding
) : BaseMviView<Model, Event>(), MonthModePeriodPickerView {

    private val itemClickListener = CalendarListener(::dispatch)
    private var startPeriod: Calendar? = null
    private var endPeriod: Calendar? = null

    init {
        binding.calendar.adapter = MonthModePeriodPickerAdapter(itemClickListener)
        binding.calendar.layoutManager = CalendarLayoutManager(
            binding.root.context,
            binding.calendar.adapter as SpanSizeProvider
        )

        binding.currentDate.setOnClickListener {
            if ((binding.calendar.adapter as MonthModePeriodPickerAdapter).getEnabled()) {
                dispatch(Event.ResetSelection)
            }

            dispatch(Event.UpdateScrollToCurrentDay)
        }
    }

    override fun render(model: Model) {
        updateCalendar(model.getCalendar())

        startPeriod = model.getStartPeriod()
        endPeriod = model.getEndPeriod()
    }

    /** Обновить календарь. */
    private fun updateCalendar(storage: List<DayItemModel>) {
        val monthAdapter = (binding.calendar.adapter as MonthModePeriodPickerAdapter)
        if (monthAdapter.itemCount == 0 && storage.isNotEmpty()) {
            monthAdapter.update(storage)
        }
    }
}