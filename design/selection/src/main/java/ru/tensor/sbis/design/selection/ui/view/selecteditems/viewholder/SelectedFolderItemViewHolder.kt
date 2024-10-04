package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedFolderItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemView

/**
 * Реализация [SelectedItemViewHolder] для выбранного подразделения
 *
 * @author us.bessonov
 */
internal class SelectedFolderItemViewHolder(
    parentView: ViewGroup,
    @Px
    maxItemWidth: Int,
) : SelectedItemViewHolder<SelectedFolderItem>(
    maxItemWidth,
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.selection_selected_folder_item, parentView, false) as SelectedItemView
) {

    private val title = itemView.findViewById<TextView>(R.id.title)
    private val folder = itemView.findViewById<TextView>(R.id.folder)
    private val closeIcon = itemView.findViewById<View>(R.id.close_icon)

    override fun bind() {
        title.apply {
            text = item.name
        }
        closeIcon.setOnClickListener { item.onClick() }
    }
}