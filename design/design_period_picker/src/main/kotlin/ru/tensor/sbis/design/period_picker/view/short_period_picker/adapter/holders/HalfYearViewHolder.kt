package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders

import android.view.View
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerHalfYearItemBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerSelectionParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerParams
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToHalfYearResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.selectYear
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.setQuantumListener
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.updateSelection
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.updateQuantum
import ru.tensor.sbis.design.period_picker.view.utils.halfYearStep
import ru.tensor.sbis.design.period_picker.view.utils.monthRange
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import java.util.Calendar.*

/**
 * ViewHolder для выбора полугодий.
 *
 * @author mb.kruglova
 */
internal class HalfYearViewHolder(
    itemView: View,
    private val binding: ShortPeriodPickerHalfYearItemBinding,
    private val isEnabled: Boolean,
    private val selection: PeriodPickerParams?,
    private val displayedRange: SbisPeriodPickerRange?
) : BaseViewHolder<ShortPeriodPickerItem.HalfYearItem>(itemView),
    QuantumSelection {

    private lateinit var item: ShortPeriodPickerItem.HalfYearItem

    override fun bind(
        item: ShortPeriodPickerItem.HalfYearItem,
        position: Int,
        listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit
    ) {
        this.item = item
        this.currentPosition = position
        this.listener = listener

        setAvailability(item.year)

        if (selection != null && selection.startDate.get(YEAR) == item.year) {
            isSelected = true
            val month = selection.startDate.get(MONTH)
            if (selection.selectionType == SelectionType.YEAR) {
                selectYear()
            } else {
                updateSelection(
                    binding.selectionView,
                    binding.rootContainer,
                    mapMonthToHalfYearResId(month),
                    binding.rootContainer.id,
                    true
                )
            }
        }
    }

    override fun getQuantumSelectionParams(
        startMonth: Int,
        endMonth: Int,
        horizontalViewId: Int,
        isParentConstraint: Boolean
    ): PeriodPickerSelectionParams {
        return PeriodPickerSelectionParams(
            binding.selectionView,
            binding.rootContainer,
            startMonth,
            endMonth,
            horizontalViewId,
            item,
            currentPosition,
            listener,
            true
        )
    }

    override fun selectYear() {
        selectYear(
            binding.selectionView,
            binding.rootContainer,
            R.id.half_year_1_title,
            R.id.half_year_2_title
        )
    }

    override fun setSelection() {
        selectYear()
    }

    override fun resetSelection() {
        binding.selectionView.visibility = View.GONE
    }

    /** Настроить слушателей для вью. */
    private fun setClickListeners() {
        binding.halfYear1Title.setHalfYearListener(JANUARY, JUNE)
        binding.halfYear2Title.setHalfYearListener(JULY, DECEMBER)
    }

    /**
     * Настроить слушателя для выбора полугодия.
     */
    private fun View.setHalfYearListener(
        startMonth: Int,
        endMonth: Int
    ) = this.setQuantumListener(getQuantumSelectionParams(startMonth, endMonth, binding.rootContainer.id, true))

    /** @SelfDocumented */
    private fun setAvailability(year: Int) {
        if (isEnabled) setClickListeners()
        if (displayedRange != null) {
            for (m in monthRange step halfYearStep + 1) {
                updateQuantum(displayedRange, m, year, halfYearStep) { getSbisTextViewByHalfYear(m) }
            }
        }
    }

    /** @SelfDocumented */
    private fun getSbisTextViewByHalfYear(month: Int): SbisTextView {
        return if (month <= JUNE) binding.halfYear1Title else binding.halfYear2Title
    }
}