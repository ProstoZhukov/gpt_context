package ru.tensor.sbis.design.context_menu.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.dividers.Divider
import ru.tensor.sbis.design.context_menu.dividers.DividerType
import ru.tensor.sbis.design.context_menu.ClickableItem
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder

/**
 * ViewHolder разделителя [SbisMenu].
 *
 * @author ma.kolpakov
 */
internal class DividerViewHolder(view: View, styleHolder: SbisMenuStyleHolder) : BaseViewHolder(view, styleHolder) {
    private val slimSize = styleHolder.dividerSlimSize
    private val boldSize = styleHolder.dividerBoldSize

    override fun bind(item: Item, clickListener: ((item: ClickableItem) -> Unit)?) {
        if (item !is Divider) return

        itemView.updateLayoutParams<ViewGroup.LayoutParams> {
            height = if (item.type == DividerType.SLIM) slimSize else boldSize
        }
    }
}