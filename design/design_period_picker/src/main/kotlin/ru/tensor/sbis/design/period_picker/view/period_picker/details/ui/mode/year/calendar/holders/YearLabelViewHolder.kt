package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.holders

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.getDimen

/**
 * ViewHolder ячейки с заголовком года.
 *
 * @author mb.kruglova
 */
internal class YearLabelViewHolder(private val view: SbisTextView) : RecyclerView.ViewHolder(view) {

    /** Метод для связывания данных с ViewHolder. */
    internal fun bind(year: Int) = with(view) {
        text = year.toString()
    }

    companion object {

        val ITEM_TYPE = R.id.date_picker_year_label_item_type_id

        /** @SelfDocumented */
        internal fun create(parent: ViewGroup) =
            YearLabelViewHolder(
                SbisTextView(parent.context).apply {
                    id = R.id.date_picker_year_label_item

                    val height = InlineHeight.XL.getDimenPx(parent.context)

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        height
                    )

                    gravity = Gravity.CENTER
                    setTextColor(StyleColor.PRIMARY.getTextColor(context))
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.getDimen(R.attr.HeaderLabel_textSize)
                    )
                    typeface = TypefaceManager.getRobotoMediumFont(parent.context)
                }
            )
    }
}