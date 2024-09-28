package ru.tensor.sbis.design.design_menu.viewholders

import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.design_menu.api.ClickableItem
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.utils.SbisMenuStyleHolder

/**
 * Базовый ViewHolder элементов [SbisMenu].
 *
 * @author ra.geraskin
 */
internal abstract class BaseViewHolder(
    private val view: View,
    val styleHolder: SbisMenuStyleHolder
) :
    RecyclerView.ViewHolder(view) {

    /** @SelfDocumented */
    abstract fun bind(
        item: MenuItem,
        selectionEnabled: Boolean,
        hasMenuItems: Boolean,
        clickListener: ((item: ClickableItem) -> Unit)?
    )

    /** Установить ширину View */
    fun setWidth(width: Int) {
        view.updateLayoutParams {
            this.width = width.coerceIn(styleHolder.minItemWidth..styleHolder.maxItemWidth)
        }
    }
}