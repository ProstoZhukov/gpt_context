package ru.tensor.sbis.design.design_menu.viewholders

import android.view.ViewGroup
import ru.tensor.sbis.design.design_menu.CustomViewMenuItem
import ru.tensor.sbis.design.design_menu.api.ClickableItem
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.databinding.MenuCustomItemBinding
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder

/**
 * Кастомная реализация для [BaseViewHolder].
 *
 * @author ra.geraskin
 */
internal class CustomViewHolder(
    binding: MenuCustomItemBinding,
    styleHolder: SbisMenuStyleHolder
) : BaseViewHolder(binding.root, styleHolder) {

    override fun bind(
        item: MenuItem,
        selectionEnabled: Boolean,
        hasMenuItems: Boolean,
        clickListener: ((item: ClickableItem) -> Unit)?
    ) {

        if (item !is CustomViewMenuItem) return
        (itemView as ViewGroup).apply {
            removeAllViews()
            addView(item.factory(context))
        }

        itemView.setOnClickListener {
            clickListener?.invoke(item)
        }
    }
}