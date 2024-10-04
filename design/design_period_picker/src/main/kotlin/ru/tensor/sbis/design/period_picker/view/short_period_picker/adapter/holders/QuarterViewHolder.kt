package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders

import android.view.View
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerQuarterItemBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerSelectionParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerParams
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToHalfYearResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToQuarterResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.setQuantumListener
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.updateQuantum
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.updateSelection
import ru.tensor.sbis.design.period_picker.view.utils.halfYearStep
import ru.tensor.sbis.design.period_picker.view.utils.monthRange
import ru.tensor.sbis.design.period_picker.view.utils.quarter1Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter2Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter3Range
import ru.tensor.sbis.design.period_picker.view.utils.quarterStep
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import java.util.Calendar.*

/**
 * ViewHolder для выбора квартала и полугодия.
 *
 * @author mb.kruglova
 */
internal class QuarterViewHolder(
    itemView: View,
    private val binding: ShortPeriodPickerQuarterItemBinding,
    private val isEnabled: Boolean,
    private val selection: PeriodPickerParams?,
    private val displayedRange: SbisPeriodPickerRange?
) : BaseViewHolder<ShortPeriodPickerItem.QuarterItem>(itemView),
    QuantumSelection {

    private lateinit var item: ShortPeriodPickerItem.QuarterItem

    override fun bind(
        item: ShortPeriodPickerItem.QuarterItem,
        position: Int,
        listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit
    ) {
        this.item = item
        this.currentPosition = position
        this.listener = listener

        if (!item.isHalfYearVisible) binding.halfYearGroup.visibility = View.GONE

        setAvailability(item.year)

        if (selection != null && selection.startDate.get(YEAR) == item.year) {
            isSelected = true
            val month = selection.startDate.get(MONTH)
            when (selection.selectionType) {
                SelectionType.HALF_YEAR -> updateSelection(
                    binding.selectionView,
                    binding.rootContainer,
                    mapMonthToHalfYearResId(month),
                    binding.rootContainer.id,
                    true
                )
                SelectionType.YEAR -> selectYear()
                else -> updateSelection(
                    binding.selectionView,
                    binding.rootContainer,
                    mapMonthToQuarterResId(month),
                    R.id.half_year_1_divider,
                    !item.isHalfYearVisible
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
            isParentConstraint
        )
    }

    override fun selectYear() {
        updateSelection(
            binding.selectionView,
            binding.rootContainer,
            R.id.quarter_1_title,
            binding.rootContainer.id,
            true,
            R.id.quarter_4_title
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
        binding.quarter1Title.setQuarterListener(JANUARY, MARCH)
        binding.quarter2Title.setQuarterListener(APRIL, JUNE)
        binding.quarter3Title.setQuarterListener(JULY, SEPTEMBER)
        binding.quarter4Title.setQuarterListener(OCTOBER, DECEMBER)
        binding.halfYear1Title.setHalfYearListener(JANUARY, JUNE)
        binding.halfYear2Title.setHalfYearListener(JULY, DECEMBER)
    }

    /**
     * Настроить слушателя для выбора квартала.
     */
    private fun View.setQuarterListener(
        startMonth: Int,
        endMonth: Int
    ) = this.setQuantumListener(getQuantumSelectionParams(startMonth, endMonth, R.id.half_year_barrier))

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
            for (m in monthRange step quarterStep + 1) {
                updateQuantum(displayedRange, m, year, quarterStep) { getSbisTextViewByQuarter(m) }
            }

            for (m in monthRange step halfYearStep + 1) {
                updateQuantum(displayedRange, m, year, halfYearStep) { getSbisTextViewByHalfYear(m) }
            }
        }
    }

    /** @SelfDocumented */
    private fun getSbisTextViewByQuarter(month: Int): SbisTextView {
        return when (month) {
            in quarter1Range -> binding.quarter1Title
            in quarter2Range -> binding.quarter2Title
            in quarter3Range -> binding.quarter3Title
            else -> binding.quarter4Title
        }
    }

    /** @SelfDocumented */
    private fun getSbisTextViewByHalfYear(month: Int): SbisTextView {
        return if (month <= JUNE) binding.halfYear1Title else binding.halfYear2Title
    }
}