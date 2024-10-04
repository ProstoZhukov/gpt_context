package ru.tensor.sbis.design.context_menu.viewholders

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.context_menu.ClickableItem
import ru.tensor.sbis.design.context_menu.CustomViewItem
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder

/**
 * Кастомная реализация для [BaseViewHolder].
 *
 * @author ma.kolpakov
 */
internal class CustomViwViewHolder(view: View, styleHolder: SbisMenuStyleHolder) : BaseViewHolder(view, styleHolder) {

    override fun bind(item: Item, clickListener: ((item: ClickableItem) -> Unit)?) {
        if (item !is CustomViewItem) return

        (itemView as ViewGroup).apply {
            removeAllViews()
            addView(item.factory(context))
        }

        itemView.setOnClickListener {
            clickListener?.invoke(item)
        }
    }
}