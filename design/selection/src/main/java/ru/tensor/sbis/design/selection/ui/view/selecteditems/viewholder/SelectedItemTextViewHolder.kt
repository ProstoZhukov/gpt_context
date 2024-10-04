package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedTextItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemView

/**
 * Стандартный вьюхолдер для [SelectedTextItem]
 *
 * @author us.bessonov
 */
internal class SelectedItemTextViewHolder(
    parentView: ViewGroup,
    @Px
    maxItemWidth: Int
) : SelectedItemViewHolder<SelectedTextItem>(
    maxItemWidth,
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.selection_selected_text_item, parentView, false) as SelectedItemView
) {

    private val title = itemView.findViewById<TextView>(R.id.title)
    private val closeIcon = itemView.findViewById<View>(R.id.close_icon)

    override fun bind() {
        title.run {
            text = item.text
        }
        closeIcon.setOnClickListener { item.onClick() }
    }
}
