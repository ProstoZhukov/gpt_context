package ru.tensor.sbis.design.design_menu.quick_action_menu

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.design_menu.QuickActionMenuItem
import ru.tensor.sbis.design.design_menu.QuickActionMenu
import ru.tensor.sbis.design.design_menu.databinding.QuickActionMenuItemBinding
import ru.tensor.sbis.design.utils.extentions.setHorizontalMargin
import ru.tensor.sbis.design.utils.extentions.setRightPadding

/**
 * Реализация для элемента меню быстрых действий [QuickActionMenu].
 *
 * @author ra.geraskin
 */
internal class QuickActionMenuItemViewHolder(
    private val binding: QuickActionMenuItemBinding,
    val styleHolder: QuickActionMenuStyleHolder
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: QuickActionMenuItem, clickListener: ((item: QuickActionMenuItem) -> Unit)?) = with(binding) {

        quickActionMenuTitle.text = item.title.getString(itemView.context)
        val titleColor = item.titleColor?.getColor(root.context) ?: styleHolder.textColor
        quickActionMenuTitle.setTextColor(titleColor)
        quickActionMenuTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, styleHolder.maxTextSize.toFloat())

        itemView.setOnClickListener {
            clickListener?.invoke(item)
        }

        // Используется GradientDrawable из-за наличия возможности скругления углов.
        root.background = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(styleHolder.itemBackgroundColor, styleHolder.itemBackgroundColor)
        ).apply {
            cornerRadius = styleHolder.quickActionMenuCornerRadius.toFloat()
        }

        root.setRightPadding(styleHolder.quickActionMenuItemInnerOffset)
        quickActionMenuIcon.setHorizontalMargin(styleHolder.imageOffset, styleHolder.imageOffset)
        root.updateLayoutParams {
            height = styleHolder.quickActionMenuItemHeight
            width = styleHolder.maxItemWidth
        }

        val iconColor = item.imageColor?.getColor(root.context) ?: styleHolder.iconColor
        bindImage(quickActionMenuIcon, item, iconColor)
    }

    private fun bindImage(image: ImageView, item: QuickActionMenuItem, @ColorInt iconColor: Int) {
        if (item.image == null) return
        image.setImageDrawable(
            IconicsDrawable(itemView.context, item.image).apply {
                sizePx(styleHolder.iconSize)
                color(iconColor)
            }
        )
    }
}
