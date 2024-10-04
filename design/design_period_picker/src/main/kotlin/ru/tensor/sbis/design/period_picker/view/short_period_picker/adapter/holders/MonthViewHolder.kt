package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders

import android.view.View
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerMonthItemBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerSelectionParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerParams
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToHalfYearResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToMonthResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.mapMonthToQuarterResId
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.setQuantumListener
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.updateSelection
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils.updateQuantum
import ru.tensor.sbis.design.period_picker.view.utils.halfYearStep
import ru.tensor.sbis.design.period_picker.view.utils.monthRange
import ru.tensor.sbis.design.period_picker.view.utils.monthStep
import ru.tensor.sbis.design.period_picker.view.utils.quarter1Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter2Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter3Range
import ru.tensor.sbis.design.period_picker.view.utils.quarterStep
import ru.tensor.sbis.design.period_picker.view.utils.startHalfYearMonths
import ru.tensor.sbis.design.period_picker.view.utils.startQuarterMonths
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import java.util.Calendar.*

/**
 * ViewHolder для выбора месяца, квартала и полугодий.
 *
 * @author mb.kruglova
 */
internal class MonthViewHolder(
    itemView: View,
    private val binding: ShortPeriodPickerMonthItemBinding,
    private val isEnabled: Boolean,
    private val selection: PeriodPickerParams?,
    private val displayedRange: SbisPeriodPickerRange?
) : BaseViewHolder<ShortPeriodPickerItem.MonthItem>(itemView),
    QuantumSelection {

    private lateinit var item: ShortPeriodPickerItem.MonthItem

    override fun bind(
        item: ShortPeriodPickerItem.MonthItem,
        position: Int,
        listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit
    ) {
        this.item = item
        this.currentPosition = position
        this.listener = listener

        if (!item.isQuarterVisible) binding.quarterGroup.visibility = View.GONE
        if (!item.isHalfYearVisible) binding.halfYearGroup.visibility = View.GONE

        setAvailability(item.year)

        if (selection != null && selection.startDate.get(YEAR) == item.year) {
            isSelected = true
            val month = selection.startDate.get(MONTH)
            when (selection.selectionType) {
                SelectionType.QUARTER -> updateSelection(
                    selectionView,
                    rootContainer,
                    mapMonthToQuarterResId(month),
                    R.id.half_year_1_divider,
                    !item.isHalfYearVisible
                )

                SelectionType.HALF_YEAR -> updateSelection(
                    selectionView,
                    rootContainer,
                    mapMonthToHalfYearResId(month),
                    rootContainer.id,
                    true
                )

                SelectionType.YEAR -> selectYear()
                else -> updateSelection(
                    selectionView,
                    rootContainer,
                    mapMonthToMonthResId(month),
                    R.id.quarter_1_divider,
                    !item.isQuarterVisible && !item.isHalfYearVisible
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
            selectionView,
            rootContainer,
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
            selectionView,
            rootContainer,
            R.id.january_title,
            rootContainer.id,
            true,
            R.id.december_title
        )
    }

    override fun setSelection() {
        selectYear()
    }

    override fun resetSelection() {
        selectionView.visibility = View.GONE
    }

    /** Настроить слушателей для вью. */
    private fun setClickListeners() {
        binding.januaryTitle.setMonthListener(JANUARY)
        binding.februaryTitle.setMonthListener(FEBRUARY)
        binding.marchTitle.setMonthListener(MARCH)
        binding.aprilTitle.setMonthListener(APRIL)
        binding.mayTitle.setMonthListener(MAY)
        binding.juneTitle.setMonthListener(JUNE)
        binding.julyTitle.setMonthListener(JULY)
        binding.augustTitle.setMonthListener(AUGUST)
        binding.septemberTitle.setMonthListener(SEPTEMBER)
        binding.octoberTitle.setMonthListener(OCTOBER)
        binding.novemberTitle.setMonthListener(NOVEMBER)
        binding.decemberTitle.setMonthListener(DECEMBER)

        binding.quarter1Title.setQuarterListener(JANUARY, MARCH)
        binding.quarter2Title.setQuarterListener(APRIL, JUNE)
        binding.quarter3Title.setQuarterListener(JULY, SEPTEMBER)
        binding.quarter4Title.setQuarterListener(OCTOBER, DECEMBER)

        binding.halfYear1Title.setHalfYearListener(JANUARY, JUNE)
        binding.halfYear2Title.setHalfYearListener(JULY, DECEMBER)
    }

    /**
     * Настроить слушателя для выбора месяца.
     */
    private fun View.setMonthListener(
        month: Int
    ) = this.setQuantumListener(getQuantumSelectionParams(month, month, R.id.quarter_barrier))

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
            for (m in monthRange) {
                updateQuantum(displayedRange, m, year, monthStep) { getSbisTextViewByMonth(m) }

                if (m in startQuarterMonths) {
                    updateQuantum(displayedRange, m, year, quarterStep) { getSbisTextViewByQuarter(m) }
                }

                if (m in startHalfYearMonths) {
                    updateQuantum(displayedRange, m, year, halfYearStep) { getSbisTextViewByHalfYear(m) }
                }
            }
        }
    }

    /** @SelfDocumented */
    private fun getSbisTextViewByMonth(month: Int): SbisTextView {
        return when (month) {
            JANUARY -> binding.januaryTitle
            FEBRUARY -> binding.februaryTitle
            MARCH -> binding.marchTitle
            APRIL -> binding.aprilTitle
            MAY -> binding.mayTitle
            JUNE -> binding.juneTitle
            JULY -> binding.julyTitle
            AUGUST -> binding.augustTitle
            SEPTEMBER -> binding.septemberTitle
            OCTOBER -> binding.octoberTitle
            NOVEMBER -> binding.novemberTitle
            else -> binding.decemberTitle
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