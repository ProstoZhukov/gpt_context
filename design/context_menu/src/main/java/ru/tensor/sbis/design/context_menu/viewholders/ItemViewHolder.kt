package ru.tensor.sbis.design.context_menu.viewholders

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.context_menu.BaseItem
import ru.tensor.sbis.design.context_menu.ClickableItem
import ru.tensor.sbis.design.context_menu.Item
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.R
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.utils.SbisMenuStyleHolder
import ru.tensor.sbis.design.context_menu.view.IconCheckBox
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * ViewHolder элемента [SbisMenu].
 *
 * @author ma.kolpakov
 */
internal class ItemViewHolder(view: View, styleHolder: SbisMenuStyleHolder) : BaseViewHolder(view, styleHolder) {
    private val menuItemText: SbisTextView = view.findViewById(R.id.context_menu_item_text)
    private val menuItemComment: SbisTextView = view.findViewById(R.id.context_menu_item_comment)

    private val imageRight: ImageView =
        view.findViewById(R.id.context_menu_item_image_right)

    private val imageLeft: ImageView =
        view.findViewById(R.id.context_menu_item_image_left)

    private val checkBox: IconCheckBox =
        view.findViewById(R.id.context_menu_item_checkbox)
    private val root: LinearLayout = view.findViewById(R.id.context_menu_item_root)

    override fun bind(item: Item, clickListener: ((item: ClickableItem) -> Unit)?) {
        if (item !is BaseItem) return
        menuItemText.text = item.title?.getString(itemView.context)
        menuItemText.setTextColor(getTitleColor(itemView.context, item))
        menuItemText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, menuItemText.textSize.coerceAtMost(styleHolder.maxTextSize.toFloat())
        )
        itemView.setOnClickListener {
            clickListener?.invoke(item)
        }
        if (item.discoverabilityTitle != null) {
            menuItemComment.visibility = View.VISIBLE
            menuItemComment.text = item.discoverabilityTitle
        } else {
            menuItemComment.visibility = View.GONE
        }
        bindCheckBox(item)
        val imagePadding = styleHolder.iconSize + styleHolder.imageOffset

        if (item.emptyImageAlignment == null && item.image == null) return

        when {
            item.emptyImageAlignment == null && item.imageAlignment == HorizontalPosition.LEFT -> {
                imageRight.visibility = View.GONE
                imageLeft.visibility = View.VISIBLE
                bindImage(imageLeft, item)
            }

            item.emptyImageAlignment == null && item.imageAlignment == HorizontalPosition.RIGHT -> {
                imageRight.visibility = View.VISIBLE
                imageLeft.visibility = View.GONE
                bindImage(imageRight, item)
            }

            item.emptyImageAlignment != null && item.imageAlignment == HorizontalPosition.RIGHT -> {
                imageRight.visibility = View.GONE
                imageLeft.visibility = View.GONE
                menuItemText.setPadding(
                    menuItemText.paddingLeft,
                    menuItemText.paddingTop,
                    imagePadding,
                    menuItemText.bottom
                )
            }

            item.emptyImageAlignment != null && item.imageAlignment == HorizontalPosition.LEFT -> {
                imageRight.visibility = View.GONE
                imageLeft.visibility = View.GONE
                menuItemText.setPadding(
                    imagePadding,
                    menuItemText.paddingTop,
                    menuItemText.paddingRight,
                    menuItemText.bottom
                )
            }
        }
    }

    private fun bindImage(image: ImageView, item: BaseItem) {
        if (item.image == null) return
        image.setImageDrawable(
            IconicsDrawable(itemView.context, item.image).apply {
                sizePx(styleHolder.iconSize)
                color(getImageColor(image.context, item))
            }
        )
    }

    private fun bindCheckBox(item: BaseItem) {
        checkBox.setOnIcon(styleHolder.stateOnIcon)
        when (item.state) {
            MenuItemState.ON -> {
                checkBox.visibility = View.VISIBLE
                checkBox.isChecked = true
                root.updatePadding(left = styleHolder.offsetWithCheckbox)
            }

            MenuItemState.OFF -> {
                checkBox.visibility = View.GONE
                root.updatePadding(left = styleHolder.offsetWithoutCheckbox)
            }

            MenuItemState.MIXED -> {
                checkBox.visibility = View.INVISIBLE
                checkBox.isChecked = false
                root.updatePadding(left = styleHolder.offsetWithCheckbox)
            }
        }
    }

    private fun getTitleColor(context: Context, item: BaseItem) = item.titleColor.getColor(context).let {
        if (it == Color.MAGENTA || item.destructive) {
            if (item.destructive) styleHolder.destructiveTextColor else styleHolder.textColor
        } else it
    }

    private fun getImageColor(context: Context, item: BaseItem) = item.imageColor.getColor(context).let {
        if (it == Color.MAGENTA || item.destructive) {
            if (item.destructive) styleHolder.destructiveIconColor else styleHolder.iconColor
        } else it
    }
}
