package ru.tensor.sbis.design.context_menu

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisString

/**
 * Базовый класс для элементов [SbisMenu].
 *
 * @author ma.kolpakov
 */
abstract class BaseItem internal constructor(

    /** Заголовок. */
    val title: SbisString?,

    /** Иконка. */
    val image: SbisMobileIcon.Icon? = null,

    /** Цвет иконки. */
    val imageColor: SbisColor = SbisColor.NotSpecified,

    /** Выравнивание иконки. */
    val imageAlignment: HorizontalPosition = HorizontalPosition.RIGHT,

    /** Является ли элементом для удаления. */
    val destructive: Boolean = false,

    /** Комментарий. */
    internal open val discoverabilityTitle: String? = null,

    /** Скрытый элемент. */
    internal open val hidden: Boolean = false,

    /** Отключенный элемент. */
    internal open val disabled: Boolean = false,

    /**
     * Состояние элемента меню влияет на отображение галочки возле элемента,
     * изменение состояния обрабатывается только до вызова метода SbisMenu.showMenu().
     */
    open var state: MenuItemState = MenuItemState.OFF,

    /** Цвет заголовка. */
    val titleColor: SbisColor = SbisColor.NotSpecified,

    /** Если не null, иконка будет скрыта и останется соответствующий иконке отступ. */
    val emptyImageAlignment: HorizontalPosition? = null,

    override var handler: (() -> Unit)? = null
) : Item, ClickableItem

enum class MenuItemState {

    /** Есть галочка. */
    ON,

    /** Галочки нет и отступ для галочки тоже отсутствует
     * (необходимо выбирать это состояние когда выбор элемента не предусмотрен).
     */
    OFF,

    /** Галочки нет, но отступ для галочки остался
     * (необходимо выбирать это состояние когда есть возможность выбора, но элемент еще не выбран).
     */
    MIXED
}