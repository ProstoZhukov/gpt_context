package ru.tensor.sbis.design.design_menu

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.api.EMPTY_STRING
import ru.tensor.sbis.design.design_menu.api.MenuItemClickListener
import ru.tensor.sbis.design.design_menu.model.MenuItemSettings

/**
 * Базовый класс элемента меню.
 *
 * @param title Заголовок элемента Меню.
 * @param icon Иконка элемента Меню.
 * @param subTitle Комментарий элемента Меню.
 * @param settings Настройки элемента Меню.
 * @param handler Callback нажатия на элемент. (не работает в шторке, используй [MenuItemClickListener]])
 *
 * @author ra.geraskin
 */
@Parcelize
class SbisMenuItem(
    override val title: String,
    override val icon: SbisMobileIcon.Icon? = null,
    override val subTitle: String? = null,
    override val settings: MenuItemSettings = MenuItemSettings(),
    override val id: String = EMPTY_STRING,
    override var handler: (() -> Unit)? = null
) : BaseMenuItem(
    title,
    icon,
    subTitle,
    settings,
    handler,
    id = id
)