package ru.tensor.sbis.date_picker.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.date_picker.R

/**
 * Вьюхолдер пустой ячейки
 *
 * @author us.bessonov
 */
internal class ItemMonthDayEmptyViewHolder(view: View) : DatePickerViewHolder(view) {

    /** @SelfDocumented */
    fun bind() = Unit

    companion object {
        val ITEM_TYPE = R.id.date_picker_month_day_empty_item_type_id

        /** @SelfDocumented */
        fun create(parent: ViewGroup) = ItemMonthDayEmptyViewHolder(
            View(parent.context).apply {
                val size = resources.getDimensionPixelSize(R.dimen.date_picker_day_size)
                layoutParams = ViewGroup.LayoutParams(size, size)
                id = R.id.date_picker_item_month_day_empty_layout
            }
        )

    }
}