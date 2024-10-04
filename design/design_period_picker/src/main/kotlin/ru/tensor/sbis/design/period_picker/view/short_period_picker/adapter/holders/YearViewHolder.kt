package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.holders

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import ru.tensor.sbis.design.period_picker.databinding.ShortPeriodPickerYearItemBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.utils.checkRangeBelonging
import ru.tensor.sbis.design.period_picker.view.utils.firstDay
import ru.tensor.sbis.design.period_picker.view.utils.lastDay
import ru.tensor.sbis.design.period_picker.view.utils.year
import ru.tensor.sbis.design.period_picker.view.utils.yearStep
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import java.util.Calendar.*
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * ViewHolder для выбора года.
 *
 * @author mb.kruglova
 */
internal class YearViewHolder(
    itemView: View,
    private val binding: ShortPeriodPickerYearItemBinding,
    private val isEnabled: Boolean,
    private val selection: PeriodPickerParams?,
    private val displayedRange: SbisPeriodPickerRange?
) : BaseViewHolder<ShortPeriodPickerItem.YearItem>(itemView) {

    override fun bind(
        item: ShortPeriodPickerItem.YearItem,
        position: Int,
        listener: (ShortPeriodPickerItem, Int, SbisPeriodPickerRange) -> Unit
    ) {
        binding.yearTitle.text = item.year.toString()

        if (displayedRange != null) {
            val isRangePart = checkRangeBelonging(displayedRange, JANUARY, item.year, yearStep)
            if (!isRangePart) {
                binding.yearTitle.apply {
                    isEnabled = false
                    setOnClickListener(null)
                    setTextColor(TextColor.READ_ONLY.getValue(this.context))
                }
            }
        }

        if (item.isYearVisible && isEnabled && binding.yearTitle.isEnabled) {
            binding.yearTitle.setOnClickListener {
                val dateStart: Calendar = GregorianCalendar(item.year, JANUARY, firstDay)
                val dateEnd: Calendar = GregorianCalendar(item.year, DECEMBER, lastDay)
                listener(item, position, SbisPeriodPickerRange(dateStart, dateEnd))
            }
        }

        if (item.isHeader || item.year == getInstance().year) {
            binding.yearTitle.setTextColor(StyleColor.PRIMARY.getTextColor(itemView.context))
            val typeface: Typeface? =
                ResourcesCompat.getFont(itemView.context, ru.tensor.sbis.design.R.font.roboto_medium)
            binding.yearTitle.setTypeface(typeface, 0)
        }

        if (!item.isHeader) {
            binding.yearTitle.gravity = Gravity.CENTER

            if (selection != null && selection.startDate.get(YEAR) == item.year) {
                isSelected = true
                setSelection()
            }
        }
    }

    override fun setSelection() {
        if (!binding.selectionView.isVisible) binding.selectionView.visibility = View.VISIBLE
    }

    override fun resetSelection() {
        binding.selectionView.visibility = View.GONE
    }
}