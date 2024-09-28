package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders.HalfYearViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.HalfYearModel

/**
 * Делегат для ячейки с полугодием.
 *
 * @author mb.kruglova
 */
internal class HalfYearDelegate(
    private val listener: CalendarListener?,
    private val isEnabled: Boolean
) : QuantumDelegate<HalfYearViewHolder, HalfYearModel>() {

    override val viewType: Int = R.id.date_picker_half_year_item_type_id

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return HalfYearViewHolder.create(parent, listener, isEnabled)
    }

    override fun onBindViewHolder(holder: HalfYearViewHolder, item: HalfYearModel) = holder.bind(item)
}