package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeMonthItemBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.MonthModel
import ru.tensor.sbis.design.period_picker.view.utils.setSbisTextViewColor
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * ViewHolder ячейки с месяцем.
 *
 * @author mb.kruglova
 */
internal class MonthViewHolder(
    private val binding: PeriodPickerYearModeMonthItemBinding,
    private val listener: CalendarListener?,
    private val isEnabled: Boolean
) : QuantumViewHolder<MonthModel>(binding.root) {

    override fun setTitle(model: MonthModel) {
        binding.monthTitle.text = model.label
        binding.monthTitle.isEnabled = isEnabled && model.isRangePart
        binding.monthTitle.setSbisTextViewColor(
            if (model.isRangePart) TextColor.DEFAULT else TextColor.READ_ONLY
        )
        binding.monthTitle.setOnClickListener {
            if (binding.monthTitle.isEnabled) {
                listener?.onClickMonth(model.year, model.month)
            }
        }
    }

    companion object {
        /** @SelfDocumented */
        fun create(
            parent: ViewGroup,
            listener: CalendarListener?,
            isEnabled: Boolean
        ): QuantumViewHolder<MonthModel> {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PeriodPickerYearModeMonthItemBinding.inflate(inflater, parent, false)
            return MonthViewHolder(binding, listener, isEnabled)
        }
    }
}