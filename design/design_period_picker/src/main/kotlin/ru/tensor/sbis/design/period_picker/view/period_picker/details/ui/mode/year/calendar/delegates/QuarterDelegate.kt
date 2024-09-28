package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders.QuarterViewHolder
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.QuarterModel

/**
 * Делегат для ячейки с кварталом.
 *
 * @author mb.kruglova
 */
internal class QuarterDelegate(
    private val listener: CalendarListener?,
    private val isEnabled: Boolean
) : QuantumDelegate<QuarterViewHolder, QuarterModel>() {

    override val viewType: Int = R.id.date_picker_quarter_item_type_id

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return QuarterViewHolder.create(parent, listener, isEnabled)
    }

    override fun onBindViewHolder(holder: QuarterViewHolder, item: QuarterModel) = holder.bind(item)
}