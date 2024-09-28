package ru.tensor.sbis.design.retail_views.popup_menu

import ru.tensor.sbis.design.retail_views.popup_menu.config.DisplayOptions
import ru.tensor.sbis.design.retail_views.popup_menu.config.MenuItemWrapper

/** Параметры для инициализации меню действий. */
data class PopupMenuConfiguration(
    /** Размеры окна с меню. */
    val displayOptions: DisplayOptions = DisplayOptions(),

    /** Пункты меню. */
    val menuItems: List<MenuItemWrapper>
)