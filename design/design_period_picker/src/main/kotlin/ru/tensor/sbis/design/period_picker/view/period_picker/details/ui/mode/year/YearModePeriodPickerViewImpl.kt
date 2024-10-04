package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year

import android.view.View
import com.arkivanov.mvikotlin.core.view.BaseMviView
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeFragmentBinding
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.YearModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header.YearLabelAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView.Model
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.YearLabelListener
import java.util.Calendar

/**
 * Обертка над [View] фрагмента большого выбора периода в режиме Год.
 *
 * @author mb.kruglova
 */
internal class YearModePeriodPickerViewImpl(
    private val binding: PeriodPickerYearModeFragmentBinding
) : BaseMviView<Model, Event>(), YearModePeriodPickerView {

    private var startPeriod: Calendar? = null
    private var endPeriod: Calendar? = null

    /**
     * Слушатель событий при интеракте с квантами календаря.
     */
    private val quantumClickListener = CalendarListener(::dispatch)

    /**
     * Слушатель событий при интеракте с заголовками годов в шапке календаря.
     */
    private val yearLabelClickListener = YearLabelListener(::dispatch)

    init {
        binding.calendar.adapter = YearModePeriodPickerAdapter(quantumClickListener)
        binding.yearLabels.adapter = YearLabelAdapter(yearLabelClickListener)
    }

    override fun render(model: Model) {
        updateCalendar(model.getCalendarStorage())

        startPeriod = model.getStartPeriod()
        endPeriod = model.getEndPeriod()
    }

    /** Обновить календарь. */
    private fun updateCalendar(storage: CalendarStorage) {
        val periodPickerAdapter = binding.calendar.adapter as? YearModePeriodPickerAdapter
        if (periodPickerAdapter?.itemCount == 0) {
            periodPickerAdapter.update(storage.getYearModeCalendar())
            (binding.yearLabels.adapter as? YearLabelAdapter)?.update(storage.yearLabelsGrid)
        }
    }
}