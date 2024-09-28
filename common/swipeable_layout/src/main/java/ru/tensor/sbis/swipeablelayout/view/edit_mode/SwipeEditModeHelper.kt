/**
 * @author vv.chekurda
 */
package ru.tensor.sbis.swipeablelayout.view.edit_mode

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.DefaultMenuItem
import ru.tensor.sbis.swipeablelayout.DefaultSwipeMenu
import ru.tensor.sbis.swipeablelayout.MoveToFolderIcon
import ru.tensor.sbis.swipeablelayout.SwipeMenu
import ru.tensor.sbis.swipeablelayout.UnreadIcon
import ru.tensor.sbis.swipeablelayout.api.SwipeMenuSide
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemBindingDelegate
import ru.tensor.sbis.swipeablelayout.util.toItemVm
import ru.tensor.sbis.swipeablelayout.view.SwipeContainerLayout
import ru.tensor.sbis.swipeablelayout.view.SwipeMenuItemView
import ru.tensor.sbis.swipeablelayout.view.canvas.SwipeDismissMessageLayout
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool
import java.util.LinkedList
import ru.tensor.sbis.design.R as RDesign

/**
 * Показать preview view контейнера свайп-меню [SwipeContainerLayout] для отображения в edit mode.
 */
internal fun SwipeContainerLayout.showPreview(context: Context, attrs: AttributeSet?) {
    val colorfulContext = getColorfulContext(context)
    setMenuItemViewPool(getSwipeMenuViewPool(colorfulContext))
    setMenu(getSwipeMenu(resources))
    SwipeDismissMessageLayout(colorfulContext, attrs).also {
        it.changeTextVisibility(true)
        attachDismissMessageLayout(it, SwipeMenuSide.RIGHT)
        colorfulContext.withStyledAttributes(attrs = intArrayOf(R.attr.SwipeableLayout_menuDismissMessageBackground)) {
            it.setBackgroundColor(getColor(0, Color.TRANSPARENT))
        }
    }
}

/**
 * Показать preview view пункта меню [SwipeMenuItemView] для отображения в edit mode.
 */
internal fun SwipeMenuItemView.showPreview(
    item: DefaultMenuItem? = null
) {
    item?.toItemVm(context)?.let {
        iconText = it.icon
        it.iconColor?.let { color -> iconTextColor = color }
        it.background?.let(::setBackgroundColor)
        iconTextColor = it.iconColor ?: Color.MAGENTA
        labelText = it.label
        isLabelSingleLine = it.isLabelSingleLine
        isLabelVisible = it.isLabelVisible
    } ?: run {
        iconText = resources.getString(RDesign.string.design_mobile_icon_delete_item)
        labelText = resources.getString(R.string.design_swipe_menu_label_remove)
        setBackgroundResource(R.color.swipeable_layout_item_background_red)
        val whiteColor = ContextCompat.getColor(context, RDesign.color.palette_color_white0)
        iconTextColor = whiteColor
        labelTextColor = whiteColor
    }
}

private fun getColorfulContext(context: Context): Context = ThemeContextBuilder(
    context, defStyleAttr = R.attr.swipeableLayoutTheme, defaultStyle = R.style.ColorfulSwipeableLayoutTheme
).build()

private fun getSwipeMenuViewPool(context: Context): SwipeMenuViewPool =
    SwipeMenuViewPool.createForItemsWithIcon(context, 10)

private fun getSwipeMenu(resources: Resources): SwipeMenu<DefaultMenuItem> {
    val items = LinkedList<DefaultMenuItem>().also {
        it.add(
            DefaultMenuItem.createWithLabel(
                resources.getString(R.string.design_swipe_menu_label_mark_unread), UnreadIcon
            )
        )
        it.add(
            DefaultMenuItem.createWithLabel(
                resources.getString(R.string.design_swipe_menu_label_move), MoveToFolderIcon
            )
        )
        it.add(
            DefaultMenuItem.createRemoveOption(
                resources.getString(R.string.design_swipe_menu_label_remove)
            )
        )
    }
    return DefaultSwipeMenu(items, itemBindingDelegate = object : SwipeMenuItemBindingDelegate<DefaultMenuItem> {
        override fun bind(itemView: View, item: DefaultMenuItem) {
            (itemView as SwipeMenuItemView).showPreview(item)
        }
    })
}