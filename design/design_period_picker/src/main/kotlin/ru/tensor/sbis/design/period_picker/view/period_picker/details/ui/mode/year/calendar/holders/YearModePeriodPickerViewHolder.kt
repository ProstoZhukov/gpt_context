package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeItemBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.YearModePeriodPickerModel

/**
 * ViewHolder ячейки с годом.
 *
 * @author mb.kruglova
 */
internal class YearModePeriodPickerViewHolder(
    private val binding: PeriodPickerYearModeItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    /** Метод для связывания данных с ViewHolder. */
    internal fun bind(
        model: YearModePeriodPickerModel,
        listener: CalendarListener?,
        isEnabled: Boolean
    ) {
        binding.monthRecyclerView.updateAdapter(model.months, listener, isEnabled)
        binding.quarterRecyclerView.updateAdapter(model.quarters, listener, isEnabled)
        binding.halfYearRecyclerView.updateAdapter(model.halfYear, listener, isEnabled)
    }

    companion object {
        val ITEM_TYPE = R.id.date_picker_year_item_type_id

        fun create(
            parent: ViewGroup
        ): YearModePeriodPickerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PeriodPickerYearModeItemBinding.inflate(inflater, parent, false)
            return YearModePeriodPickerViewHolder(binding)
        }
    }
}