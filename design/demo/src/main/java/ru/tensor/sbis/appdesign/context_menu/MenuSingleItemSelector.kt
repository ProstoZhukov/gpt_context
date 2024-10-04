package ru.tensor.sbis.appdesign.context_menu

import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.SbisMenu

/**
 * @author ma.kolpakov
 */
class MenuSingleItemSelector {
    var selectedItem = -1
        private set

    fun apply(menu: SbisMenu) {
        menu.children.forEachIndexed { index, item ->
            if (index == selectedItem) {
                item.state = MenuItemState.ON
            } else {
                item.state = MenuItemState.MIXED
            }
            val oldHandler = item.handler
            item.handler = {
                oldHandler?.invoke()
                selectedItem = index
            }
        }
    }
}