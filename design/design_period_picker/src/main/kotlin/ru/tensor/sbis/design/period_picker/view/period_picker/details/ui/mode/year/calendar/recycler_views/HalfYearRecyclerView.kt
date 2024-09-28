package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.recycler_views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.QuantumAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates.HalfYearDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.HalfYearModel

/**
 * RecyclerView для вывода кварталов/полугодий в календарной сетке.
 *
 * @author mb.kruglova
 */
internal class HalfYearRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        isNestedScrollingEnabled = false
    }

    internal fun updateAdapter(items: List<HalfYearModel>, listener: CalendarListener?, isEnabled: Boolean) {
        val quantumAdapter = QuantumAdapter(HalfYearDelegate(listener, isEnabled))
        quantumAdapter.reload(items)

        layoutManager = LinearLayoutManager(context)
        adapter = quantumAdapter
    }
}