package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerYearModeHalfYearItemBinding
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.HalfYearModel
import ru.tensor.sbis.design.period_picker.view.utils.setSbisTextViewColor
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * ViewHolder ячейки с полугодием.
 *
 * @author mb.kruglova
 */
internal class HalfYearViewHolder(
    private val binding: PeriodPickerYearModeHalfYearItemBinding,
    private val listener: CalendarListener?,
    private val isEnabled: Boolean
) : QuantumViewHolder<HalfYearModel>(binding.root) {

    override fun setTitle(model: HalfYearModel) {
        binding.halfYearTitle.text = model.label
        binding.halfYearTitle.isEnabled = isEnabled && model.isRangePart
        binding.halfYearTitle.setSbisTextViewColor(
            if (model.isRangePart) TextColor.DEFAULT else TextColor.READ_ONLY
        )
        binding.halfYearTitle.setOnClickListener {
            if (binding.halfYearTitle.isEnabled) {
                listener?.onClickHalfYear(model.year, model.month)
            }
        }
    }

    companion object {
        /** @SelfDocumented */
        fun create(
            parent: ViewGroup,
            listener: CalendarListener?,
            isEnabled: Boolean
        ): HalfYearViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PeriodPickerYearModeHalfYearItemBinding.inflate(inflater, parent, false)
            return HalfYearViewHolder(binding, listener, isEnabled)
        }
    }
}