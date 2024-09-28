package ru.tensor.sbis.swipeablelayout.util

import androidx.annotation.StringRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.swipeablelayout.api.menu.IconItem
import ru.tensor.sbis.swipeablelayout.api.menu.IconWithLabelItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle

/**
 * Фабрика для создания наиболее часто используемых конфигурация элементов свайп-меню
 *
 * @author da.zolotarev
 */
object SwipeMenuItemFactory {

    fun createDeleteItem(
        @StringRes label: Int, clickAction: () -> Unit = { }
    ) = IconWithLabelItem(
        SwipeIcon(SbisMobileIcon.Icon.smi_SwipeDelete), label, SwipeItemStyle.RED, clickAction
    )

    fun createDeleteItem(
        label: String, clickAction: () -> Unit = { }
    ) = IconWithLabelItem(
        SwipeIcon(SbisMobileIcon.Icon.smi_SwipeDelete), label, SwipeItemStyle.RED, clickAction
    )

    fun createDeleteItem(
        clickAction: () -> Unit = { }
    ) = IconItem(
        SwipeIcon(SbisMobileIcon.Icon.smi_SwipeDelete), SwipeItemStyle.RED, clickAction
    )
}