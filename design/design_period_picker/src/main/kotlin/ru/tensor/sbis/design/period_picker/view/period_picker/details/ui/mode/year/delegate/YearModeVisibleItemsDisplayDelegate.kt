package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.delegate

import androidx.recyclerview.widget.LinearLayoutManager
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeFragmentBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.YearModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header.YearLabelAdapter
import ru.tensor.sbis.design.period_picker.view.utils.getFirstVisibleItemPosition

/**
 * Делегат для отображения видимых элементов.
 *
 * @author mb.kruglova
 */
internal class YearModeVisibleItemsDisplayDelegate(
    private val binding: PeriodPickerYearModeFragmentBinding
) {

    private val yearsLayoutManager: LinearLayoutManager?
        get() = binding.calendar.layoutManager as? LinearLayoutManager

    private val yearsAdapter: YearModePeriodPickerAdapter
        get() = binding.calendar.adapter as YearModePeriodPickerAdapter

    private val yearLabelLayoutManager: LinearLayoutManager?
        get() = binding.yearLabels.layoutManager as? LinearLayoutManager

    private val yearLabelAdapter: YearLabelAdapter
        get() = binding.yearLabels.adapter as YearLabelAdapter

    /** Получить позицию появившегося элемента. */
    internal fun getAppearedItemPosition(
        currentPosition: Int
    ): Int? {
        return getFirstVisibleItemPosition(yearsLayoutManager, yearsAdapter, currentPosition)
    }

    /** Получить год появившегося элемента. */
    internal fun getAppearedItemYear(
        firstVisiblePosition: Int,
        currentYear: Int
    ): Int? {
        val year = yearsAdapter.getYearByPosition(firstVisiblePosition)

        return if (year != currentYear) {
            yearLabelAdapter.updateYearLabel(year)
            val firstItemPosition = yearLabelLayoutManager?.findFirstVisibleItemPosition() ?: 0
            val lastItemPosition =
                yearLabelLayoutManager?.findLastVisibleItemPosition() ?: (yearLabelAdapter.itemCount - 1)
            if (year < firstItemPosition || year > lastItemPosition) {
                binding.yearLabels.scrollToPosition(yearLabelAdapter.getYearPosition(year))
            }
            year
        } else {
            null
        }
    }
}