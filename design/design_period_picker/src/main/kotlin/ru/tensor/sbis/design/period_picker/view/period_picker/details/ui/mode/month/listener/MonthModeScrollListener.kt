package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.listener

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerMonthModeFragmentBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.MonthModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.CalendarLayoutManager
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarReloadingDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CompleteCalendarReloading
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.delegate.MonthModeVisibleItemsDisplayDelegate
import ru.tensor.sbis.design.theme.global_variables.Elevation

/**
 * Слушатель событий скроллирования календаря в режиме Месяц.
 *
 * @author mb.kruglova
 */
internal class MonthModeScrollListener(
    val binding: PeriodPickerMonthModeFragmentBinding,
    val resources: Resources,
    private var isShownElevation: Boolean,
    @TestOnly private val reloadingDelegate: CalendarReloadingDelegate? = null,
    @TestOnly private val displayDelegate: MonthModeVisibleItemsDisplayDelegate? = null
) : RecyclerView.OnScrollListener(), CompleteCalendarReloading {

    private var calendarReloadingDelegate: CalendarReloadingDelegate
    private var visibleItemsDisplayDelegate: MonthModeVisibleItemsDisplayDelegate

    private val monthLayoutManager: CalendarLayoutManager?
        get() = binding.calendar.layoutManager as? CalendarLayoutManager

    private val monthAdapter: MonthModePeriodPickerAdapter
        get() = binding.calendar.adapter as MonthModePeriodPickerAdapter

    private var firstItemPosition = 0
    private var lastItemPosition = 0

    private var currentPosition = 0
    private var currentMonth: Int? = null

    companion object {
        /** Минимальное количество элементов, отображаемых календарем, до начала дозагрузки данных. */
        private const val MIN_VIEWS_TO_PAGE = 14
    }

    init {
        calendarReloadingDelegate = reloadingDelegate ?: CalendarReloadingDelegate(monthAdapter, monthLayoutManager)
        visibleItemsDisplayDelegate = displayDelegate ?: MonthModeVisibleItemsDisplayDelegate(binding, resources)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (!isShownElevation && dy != 0) {
            isShownElevation = true
            binding.divider.visibility = View.GONE
            binding.backgroundView.elevation = Elevation.XL.getDimen(binding.backgroundView.context)
        }

        calendarReloadingDelegate.reloadData(dy, MIN_VIEWS_TO_PAGE)

        if (dy != 0) {
            val (first, last) = visibleItemsDisplayDelegate.updateVisibleItems(firstItemPosition, lastItemPosition)
            firstItemPosition = first
            lastItemPosition = last

            visibleItemsDisplayDelegate.getAppearedItemPosition(currentPosition)?.let { position ->
                currentPosition = position

                visibleItemsDisplayDelegate.getAppearedItemMonth(position, currentMonth)?.let { month ->
                    currentMonth = month
                }
            }
        }
    }

    override fun completeReloading() {
        calendarReloadingDelegate.isReloading = false
    }
}