package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeFragmentBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarReloadingDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CompleteCalendarReloading
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.delegate.YearModeVisibleItemsDisplayDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.YearModePeriodPickerAdapter

/**
 * Слушатель событий скроллинга календаря в режиме Год.
 *
 * @author mb.kruglova
 */
internal class YearModeScrollListener(
    private val binding: PeriodPickerYearModeFragmentBinding,
    @TestOnly private val reloadingDelegate: CalendarReloadingDelegate? = null,
    @TestOnly private val displayDelegate: YearModeVisibleItemsDisplayDelegate? = null
) : RecyclerView.OnScrollListener(), CompleteCalendarReloading {

    private var calendarReloadingDelegate: CalendarReloadingDelegate
    private var visibleItemsDisplayDelegate: YearModeVisibleItemsDisplayDelegate
    private val yearsLayoutManager: LinearLayoutManager?
        get() = binding.calendar.layoutManager as? LinearLayoutManager

    private val yearsAdapter: YearModePeriodPickerAdapter
        get() = binding.calendar.adapter as YearModePeriodPickerAdapter

    private var currentPosition = 0
    private var currentYear = 0

    init {
        calendarReloadingDelegate = reloadingDelegate ?: CalendarReloadingDelegate(yearsAdapter, yearsLayoutManager)
        visibleItemsDisplayDelegate = displayDelegate ?: YearModeVisibleItemsDisplayDelegate(binding)
    }

    companion object {
        /** Минимальное количество элементов, отображаемых календарем, до начала дозагрузки данных. */
        private const val MIN_VIEWS_TO_PAGE = 1
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        calendarReloadingDelegate.reloadData(dy, MIN_VIEWS_TO_PAGE)

        visibleItemsDisplayDelegate.getAppearedItemPosition(currentPosition)?.let { position ->
            currentPosition = position
            visibleItemsDisplayDelegate.getAppearedItemYear(position, currentYear)?.let { year ->
                currentYear = year
            }
        }
    }

    override fun completeReloading() {
        calendarReloadingDelegate.isReloading = false
    }
}