package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.recycler_views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.QuantumAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates.MonthDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.MonthModel

/**
 * RecyclerView для вывода списка месяцев в календарной сетке.
 *
 * @author mb.kruglova
 */
internal class MonthRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        isNestedScrollingEnabled = false
        setHasFixedSize(true)
    }

    companion object {
        private const val NUMBER_OF_COLUMNS = 3
    }

    internal fun updateAdapter(items: List<MonthModel>, listener: CalendarListener?, isEnabled: Boolean) {
        val quantumAdapter = QuantumAdapter(MonthDelegate(listener, isEnabled))
        quantumAdapter.reload(items)

        layoutManager = GridLayoutManager(context, NUMBER_OF_COLUMNS)
        adapter = quantumAdapter
    }
}