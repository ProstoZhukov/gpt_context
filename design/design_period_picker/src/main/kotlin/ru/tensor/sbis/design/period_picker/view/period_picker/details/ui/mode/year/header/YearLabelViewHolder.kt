package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.YearLabelBackgroundDrawable
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.YearLabelListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.YearLabelModel
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.getDimen

/**
 * ViewHolder ячейки с заголовком года.
 *
 * @author mb.kruglova
 */
internal class YearLabelViewHolder(
    private val view: SbisTextView,
    private val listener: YearLabelListener,
    private val isEnabled: Boolean
) : RecyclerView.ViewHolder(view) {

    /** Связать вью с данными. */
    internal fun bind(model: YearLabelModel) {
        view.text = model.year.toString()
        view.isEnabled = isEnabled && model.isRangePart
        view.setOnClickListener {
            if (view.isEnabled) {
                listener.onClickYearLabel(model.year, model.month)
            }
        }
        view.setTextColor(
            when {
                model.isMarked -> getTextColor(StyleColor.PRIMARY)
                !model.isRangePart -> TextColor.READ_ONLY.getValue(view.context)
                else -> getTextColor(StyleColor.UNACCENTED)
            }
        )
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.context.getDimen(R.attr.HeaderLabel_textSize))
        val typeface: Typeface? =
            ResourcesCompat.getFont(view.context, ru.tensor.sbis.design.R.font.roboto_medium)
        view.setTypeface(typeface, 0)

        view.gravity = Gravity.CENTER

        view.background = YearLabelBackgroundDrawable(view.context).apply {
            quantumType = model.quantumSelection.quantumType
            drawableType = model.quantumSelection.drawableType
        }
    }

    /** @SelfDocumented */
    private fun getTextColor(color: StyleColor): Int {
        return color.getTextColor(view.context)
    }
}