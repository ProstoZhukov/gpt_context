package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders.MonthViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.MonthModel

/**
 * Делегат для ячейки с месяцем.
 *
 * @author mb.kruglova
 */
internal class MonthDelegate(
    private val listener: CalendarListener?,
    private val isEnabled: Boolean
) : QuantumDelegate<MonthViewHolder, MonthModel>() {

    override val viewType: Int = R.id.date_picker_month_item_type_id

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MonthViewHolder.create(parent, listener, isEnabled)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, item: MonthModel) = holder.bind(item)
}