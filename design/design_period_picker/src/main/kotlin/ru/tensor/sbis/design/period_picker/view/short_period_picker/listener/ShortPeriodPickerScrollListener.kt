package ru.tensor.sbis.design.period_picker.view.short_period_picker.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerFragmentBinding
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerListAdapter

/**
 * Слушатель событий скроллирования календаря.
 *
 * @author mb.kruglova
 */
internal class ShortPeriodPickerScrollListener(
    private val binding: ShortPeriodPickerFragmentBinding?,
    private val minYear: Int,
    @TestOnly private val linearLayoutManager: LinearLayoutManager? = null,
    @TestOnly private val listAdapter: ShortPeriodPickerListAdapter? = null
) : RecyclerView.OnScrollListener() {

    private val layoutManager: LinearLayoutManager
        get() = binding?.shortPeriodPickerList?.layoutManager as LinearLayoutManager
    private val calendarAdapter: ShortPeriodPickerListAdapter
        get() = binding?.shortPeriodPickerList?.adapter as ShortPeriodPickerListAdapter

    private var currentPosition = 0
    private var currentYear = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val adapter = listAdapter ?: calendarAdapter

        val firstVisiblePos = (linearLayoutManager ?: layoutManager).findFirstVisibleItemPosition()
        if (currentPosition != firstVisiblePos && firstVisiblePos in 0 until adapter.itemCount) {
            currentPosition = firstVisiblePos
            val item = adapter.getItemByPosition(firstVisiblePos)
            val year = if (item is ShortPeriodPickerItem.YearItem) item.year - 1 else item.year

            if (year != currentYear && year != minYear - 1) {
                currentYear = year
                binding?.shortPeriodPickerHeaderTitle?.text = year.toString()
            }
        }
    }
}