package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.delegate

import android.content.res.Resources
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerMonthModeFragmentBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.MonthModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.CalendarLayoutManager
import ru.tensor.sbis.design.period_picker.view.utils.getFirstVisibleItemPosition
import ru.tensor.sbis.design.period_picker.view.utils.getFormattedMonthLabel
import ru.tensor.sbis.design.period_picker.view.utils.mapMonthToStringResId
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar

/**
 * Делегат для отображения видимых элементов.
 *
 * @author mb.kruglova
 */
internal class MonthModeVisibleItemsDisplayDelegate(
    val binding: PeriodPickerMonthModeFragmentBinding,
    val resources: Resources
) {
    private val monthLayoutManager: CalendarLayoutManager?
        get() = binding.calendar.layoutManager as? CalendarLayoutManager

    private val monthAdapter: MonthModePeriodPickerAdapter
        get() = binding.calendar.adapter as MonthModePeriodPickerAdapter

    /** Получить позицию появившегося элемента. */
    internal fun getAppearedItemPosition(
        currentPosition: Int
    ): Int? {
        return getFirstVisibleItemPosition(monthLayoutManager, monthAdapter, currentPosition)
    }

    /** Получить месяц появившегося элемента. */
    internal fun getAppearedItemMonth(
        firstVisiblePosition: Int,
        currentMonth: Int?
    ): Int? {
        val defaultMonth = currentMonth ?: Calendar.getInstance().month
        val month = monthAdapter.getMonthByPosition(firstVisiblePosition, defaultMonth)

        return if (month != currentMonth) {
            val monthString = resources.getString(mapMonthToStringResId(if (month == -1) Calendar.DECEMBER else month))
            var year = monthAdapter.getYearByPosition(firstVisiblePosition)
            if (month == -1 && (currentMonth == 0 || currentMonth == 11)) {
                year -= 1
            }

            binding.monthLabel.text = if (year != Calendar.getInstance().year) {
                getFormattedMonthLabel(monthString, year)
            } else {
                monthString
            }
            month
        } else {
            null
        }
    }

    /** Обновить видимую часть календаря. */
    internal fun updateVisibleItems(
        prevFirstItemPosition: Int,
        prevLastItemPosition: Int
    ): Pair<Int, Int> {
        val layoutManager = monthLayoutManager ?: return prevFirstItemPosition to prevLastItemPosition
        var firstItemPosition = -1
        var lastItemPosition = -1
        val newFirstItemPosition = layoutManager.findFirstVisibleItemPosition()
        val newLastItemPosition = layoutManager.findLastVisibleItemPosition()

        val prevRange = prevFirstItemPosition..prevLastItemPosition
        val newRange = newFirstItemPosition..newLastItemPosition

        when {
            newLastItemPosition <= prevFirstItemPosition || newFirstItemPosition >= prevLastItemPosition ||
                (newFirstItemPosition <= prevFirstItemPosition && newLastItemPosition >= prevLastItemPosition) ||
                (newFirstItemPosition in prevRange && newLastItemPosition in prevRange) -> {
                firstItemPosition = newFirstItemPosition
                lastItemPosition = newLastItemPosition
            }
            prevFirstItemPosition in newRange && prevLastItemPosition !in newRange -> {
                firstItemPosition = newFirstItemPosition
                lastItemPosition = prevFirstItemPosition
            }
            prevLastItemPosition in newRange && prevFirstItemPosition !in newRange -> {
                firstItemPosition = prevLastItemPosition
                lastItemPosition = newLastItemPosition
            }
        }

        return if (firstItemPosition == prevFirstItemPosition && lastItemPosition == prevLastItemPosition) {
            prevFirstItemPosition to prevLastItemPosition
        } else {
            val startDate = monthAdapter.getDateByPosition(firstItemPosition)
            val endDate = monthAdapter.getDateByPosition(lastItemPosition)
            monthAdapter.listener?.onUpdateCounters(startDate, endDate)

            firstItemPosition to lastItemPosition
        }
    }
}