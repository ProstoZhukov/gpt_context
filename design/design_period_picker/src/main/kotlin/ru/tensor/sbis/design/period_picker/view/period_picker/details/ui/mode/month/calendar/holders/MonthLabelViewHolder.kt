package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.MonthLabelModel
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.getDimen

/**
 * ViewHolder ячейки с указанием месяца.
 *
 * @author mb.kruglova
 */
internal class MonthLabelViewHolder(
    private val view: SbisTextView,
    private val listener: CalendarListener?
) : RecyclerView.ViewHolder(view) {

    /** @SelfDocumented */
    fun bind(model: MonthLabelModel) = with(view) {
        setOnClickListener {
            if (isEnabled && model.isRangePart) listener?.onClickItem(model.date, true)
        }
        text = model.label
    }

    companion object {

        val ITEM_TYPE = R.id.date_picker_month_label_item_type_id

        /** @SelfDocumented */
        fun create(parent: ViewGroup, listener: CalendarListener?, isEnabled: Boolean) =
            MonthLabelViewHolder(
                SbisTextView(parent.context).apply {
                    id = R.id.date_picker_month_label_item

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.CENTER
                    setTextColor(TextColor.DEFAULT.getValue(context))
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.getDimen(R.attr.MonthLabel_textSize)
                    )
                    typeface = TypefaceManager.getRobotoMediumFont(parent.context)

                    this.isEnabled = isEnabled
                },
                listener
            )
    }
}