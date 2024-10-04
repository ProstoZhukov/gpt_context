package ru.tensor.sbis.design.context_menu.viewholders

import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.context_menu.ClickableItem
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder

/**
 * Базовый ViewHolder элементов [SbisMenu].
 *
 * @author ma.kolpakov
 */
internal abstract class BaseViewHolder(private val view: View, val styleHolder: SbisMenuStyleHolder) :
    RecyclerView.ViewHolder(view) {

    /** @SelfDocumented */
    abstract fun bind(item: Item, clickListener: ((item: ClickableItem) -> Unit)?)

    /** Установить ширину View */
    fun setWidth(width: Int?) {
        if (width == null) return
        view.updateLayoutParams<RecyclerView.LayoutParams> {
            this.width = width.coerceIn(styleHolder.minItemWidth..styleHolder.maxItemWidth)
        }
    }
}