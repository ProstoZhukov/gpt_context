package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.holders

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.period_picker.R

/**
 * ViewHolder пустой ячейки.
 *
 * @author mb.kruglova
 */
internal class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    /** @SelfDocumented */
    fun bind() = Unit

    companion object {

        val ITEM_TYPE = R.id.date_picker_empty_item_type_id

        /** @SelfDocumented */
        fun create(parent: ViewGroup) = EmptyViewHolder(
            View(parent.context).apply { id = R.id.date_picker_empty_item }
        )
    }
}