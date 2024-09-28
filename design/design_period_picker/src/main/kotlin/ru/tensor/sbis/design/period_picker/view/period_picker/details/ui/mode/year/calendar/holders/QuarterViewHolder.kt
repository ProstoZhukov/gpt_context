package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeQuarterItemBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.QuarterModel
import ru.tensor.sbis.design.period_picker.view.utils.setSbisTextViewColor
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * ViewHolder ячейки с кварталом.
 *
 * @author mb.kruglova
 */
internal class QuarterViewHolder(
    private val binding: PeriodPickerYearModeQuarterItemBinding,
    private val listener: CalendarListener?,
    private val isEnabled: Boolean
) : QuantumViewHolder<QuarterModel>(binding.root) {

    override fun setTitle(model: QuarterModel) {
        binding.quarterTitle.text = model.label
        binding.quarterTitle.isEnabled = isEnabled && model.isRangePart
        binding.quarterTitle.setSbisTextViewColor(
            if (model.isRangePart) TextColor.DEFAULT else TextColor.READ_ONLY
        )
        binding.quarterTitle.setOnClickListener {
            if (binding.quarterTitle.isEnabled) {
                listener?.onClickQuarter(model.year, model.month)
            }
        }
    }

    companion object {
        /** @SelfDocumented */
        fun create(
            parent: ViewGroup,
            listener: CalendarListener?,
            isEnabled: Boolean
        ): QuarterViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PeriodPickerYearModeQuarterItemBinding.inflate(inflater, parent, false)
            return QuarterViewHolder(binding, listener, isEnabled)
        }
    }
}