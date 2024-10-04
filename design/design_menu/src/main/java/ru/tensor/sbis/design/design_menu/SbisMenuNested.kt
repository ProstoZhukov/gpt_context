package ru.tensor.sbis.design.design_menu

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.api.EMPTY_STRING
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.api.MenuItemClickListener
import ru.tensor.sbis.design.design_menu.model.MenuItemSettings

/**
 * Класс вложенного меню. Все элементы внутри класса отображаются с иерархическим отступом.
 * Иерархический отступ реализовывается в плоском списке обычным отступом внутри layout элемента.
 *
 * @param children Список дочерних элементов меню.
 * @param title Заголовок элемента Меню.
 * @param icon Иконка элемента Меню.
 * @param subTitle Комментарий элемента Меню.
 * @param settings Настройки элемента Меню.
 * @param handler Callback нажатия на элемент. (не работает в шторке, используй [MenuItemClickListener]])
 *
 * @author ra.geraskin
 */
@Parcelize
class SbisMenuNested(
    val children: List<MenuItem>,
    override val title: String,
    override val icon: SbisMobileIcon.Icon? = null,
    override val subTitle: String? = null,
    override val settings: MenuItemSettings = MenuItemSettings(),
    override var handler: (() -> Unit)? = null,
    override val id: String = EMPTY_STRING
) : BaseMenuItem(
    title,
    icon,
    subTitle,
    settings,
    handler,
    id = id
) {

    /**
     * Мапинг вложенного меню в обычный айтем для отображения в плоском списке.
     */
    internal fun toSbisMenuItem(hierarchyLevel: Int) = SbisMenuItem(
        title = title,
        icon = icon,
        subTitle = subTitle,
        settings = settings,
        handler = handler,
        id = id
    ).apply {
        this.hierarchyLevel = hierarchyLevel
    }

}