package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Px
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItemWithImage
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.base.SelectedItemViewHolder
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemView
import ru.tensor.sbis.fresco_view.SuperEllipseDraweeView

/**
 * Реализация [SelectedItemViewHolder] для выбранного элемента с изображением и текстом (например, рабочая группа)
 *
 * @author us.bessonov
 */
internal class SelectedItemWithImageViewHolder(
    parentView: ViewGroup,
    @Px
    maxItemWidth: Int
) : SelectedItemViewHolder<SelectedItemWithImage>(
    maxItemWidth,
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.selection_selected_item_with_image, parentView, false) as SelectedItemView
) {

    private val title = itemView.findViewById<TextView>(R.id.title)
    private val image = itemView.findViewById<SuperEllipseDraweeView>(R.id.image)
    private val closeIcon = itemView.findViewById<View>(R.id.close_icon)

    override fun recycle() {
        super.recycle()
        image.setImageURI(null as String?)
    }

    override fun bind() {
        image.setImageURI(item.imageUrl)

        title.apply {
            text = item.text
        }
        closeIcon.setOnClickListener { item.onClick() }
    }
}