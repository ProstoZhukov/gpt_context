package ru.tensor.sbis.design.design_menu.api

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.design_menu.model.MenuItemSettings

/**
 * Базовый класс элемента меню.
 *
 * @author ra.geraskin
 */
abstract class BaseMenuItem internal constructor(

    /**
     * Заголовок элемента Меню.
     */
    open val title: String?,

    /**
     * Иконка элемента Меню.
     */
    open val icon: SbisMobileIcon.Icon?,

    /**
     * Комментарий элемента Меню.
     */
    open val subTitle: String?,

    /**
     * Настройки элемента Меню.
     */
    open val settings: MenuItemSettings,

    /**
     * Callback нажатия на элемент. Если он равен null - элемент будет некликабельным.
     *
     * ВНИМАНИЕ: при использовании данных лямбд в отобржаении в шторке убедитесь, что лямбда захватывает контекст,
     * и прочие несериализуемые объекты. При уничтожении активити, находящейся в фоне, несериализуемые объекты могут
     * вызвать падение приложения.
     *
     * В противном случае, для получения кликов используй интерфейс [MenuItemClickListener].
     */
    override var handler: (() -> Unit)? = null,

    /**
     * Уровень иерархии. Влияет на величину иерархического отступа от левого края.
     */
    internal var hierarchyLevel: Int = 0,

    /**
     * Скрытый элемент.
     * Используется прикладными разработчиками для скрытия элемента в меню.
     * Им удобнее проставить элементу флаг hidden = true чем фильтровать список айтемов и передавать только отображаемые.
     *
     * Кейсы из TS:
     * https://s.tensor.ru/result/rc-24.2139_461578_1716180391
     *
     * Кейс из официанта:
     * https://git.sbis.ru/mobileworkspace/apps/droid/waiter2/blob/rc-24.2132/app/src/main/java/ru/tensor/waiter/presentation/modules/order/byguests/view/OrderByGuestsPopupMenu.kt#L83
     */
    open val hidden: Boolean = false,

    /**
     * Идентификатор элемента меню. Необходим для работы меню в режиме шторки, для
     * сохранения состояния при повороте экрана.
     */
    open val id: String = EMPTY_STRING

) : MenuItem, ClickableItem

internal const val EMPTY_STRING = ""