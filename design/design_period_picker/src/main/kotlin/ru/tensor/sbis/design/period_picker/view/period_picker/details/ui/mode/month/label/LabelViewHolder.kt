package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.label

import android.util.TypedValue
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.getDimen

/**
 * ViewHolder меток в шапке выбора периода.
 *
 * @author mb.kruglova
 */
internal class LabelViewHolder(private val view: SbisTextView) : RecyclerView.ViewHolder(view) {

    /** Связать вью с данными. */
    fun bind(model: LabelModel) {
        view.text = model.label
        setTextColor(if (model.isMarked) StyleColor.PRIMARY else StyleColor.UNACCENTED)
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.context.getDimen(R.attr.HeaderLabel_textSize))
        view.gravity = Gravity.CENTER
    }

    private fun setTextColor(color: StyleColor) {
        view.setTextColor(color.getTextColor(view.context))
    }
}