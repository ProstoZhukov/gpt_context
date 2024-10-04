package ru.tensor.sbis.design.design_menu.viewholders

import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.api.ClickableItem
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.design_menu.view.MenuItemView

/**
 * ViewHolder элемента [SbisMenu].
 *
 * @author ra.geraskin
 */
internal class ItemViewHolder(
    private val view: MenuItemView,
    styleHolder: SbisMenuStyleHolder,
) :
    BaseViewHolder(view, styleHolder) {

    override fun bind(
        item: MenuItem,
        selectionEnabled: Boolean,
        hasMenuItems: Boolean,
        clickListener: ((item: ClickableItem) -> Unit)?
    ) {
        if (item !is BaseMenuItem) return
        view.setOnClickListener {
            clickListener?.invoke(item)
        }
        view.setParams(item, hasMenuItems, selectionEnabled)
    }
}
