package ru.tensor.sbis.design.design_menu.viewholders

import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.design_menu.api.ClickableItem
import ru.tensor.sbis.design.design_menu.dividers.Divider
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.databinding.MenuTextDividerBinding
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.theme.HorizontalAlignment.*

/**
 * ViewHolder разделителя с заголовком [SbisMenu].
 *
 * @author ra.geraskin
 */
internal class DividerViewHolder(
    private val binding: MenuTextDividerBinding,
    styleHolder: SbisMenuStyleHolder
) : BaseViewHolder(binding.root, styleHolder) {

    init {
        binding.root.updateLayoutParams<RecyclerView.LayoutParams> {
            height = styleHolder.dividerHeight
            topMargin = styleHolder.dividerMarginTop
            bottomMargin = styleHolder.dividerMarginBottom
        }
    }

    override fun bind(
        item: MenuItem,
        selectionEnabled: Boolean,
        hasMenuItems: Boolean,
        clickListener: ((item: ClickableItem) -> Unit)?
    ): Unit = with(binding) {
        (item as? Divider)?.configureDefaultLayoutDivider(menuDividerLineLeft, menuDividerText, menuDividerLineRight)
    }

}