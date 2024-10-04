package ru.tensor.sbis.design.folders.data.model

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.swipeablelayout.api.menu.IconItem
import ru.tensor.sbis.swipeablelayout.api.menu.StubItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.SwipeMenuItemFactory

/**
 * Действия над папками
 *
 * @param swipeMenuItem иконка действия для свайп меню
 *
 * @author ma.kolpakov
 */
enum class FolderActionType(internal val swipeMenuItem: SwipeMenuItem = StubItem()) {

    /** Клик по папке */
    CLICK,

    /** Переименовать папку */
    RENAME(createMenuItem(SbisMobileIcon.Icon.smi_SwipeRename, SwipeItemStyle.BLUE, "Переименовать")),

    /** Создать папку */
    CREATE(createMenuItem(SbisMobileIcon.Icon.smi_SwipeAddFolder, SwipeItemStyle.GREY, "Создать папку")),

    /** Удалить папку */
    DELETE(SwipeMenuItemFactory.createDeleteItem().apply { autotestsText = "Удалить" }),

    /** Отменить шаринг расшаренной мне папки */
    UNSHARE(createMenuItem(SbisMobileIcon.Icon.smi_UnPublish, SwipeItemStyle.BLUE, "Отменить шаринг")),

    /** Клик по дополнительной команде */
    ADDITIONAL_COMMAND_CLICK,

    /** Клик по заголовку дополнительной команды */
    ADDITIONAL_COMMAND_TITLE_CLICK,

    /** Клик по иконке дополнительной команды */
    ADDITIONAL_COMMAND_ICON_CLICK,
}

private fun createMenuItem(
    icon: SbisMobileIcon.Icon,
    style: SwipeItemStyle,
    autotestsText: String
) = IconItem(SwipeIcon(icon), style)
    .apply { this.autotestsText = autotestsText }