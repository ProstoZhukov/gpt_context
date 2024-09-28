package ru.tensor.sbis.base_components.adapter.universal.swipe

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.swipeablelayout.api.menu.*

/**
 * Интерфейс для добавления пунктов меню к компоненту свайпа по элементу списка.
 * Упрощает добавление за счет предустановленных стандартных методов.
 *
 * @author am.boldinov
 */
interface SwipeMenuItemBuilder {

    /**
     * Добавляет пунт меню "Удалить"
     */
    fun addRemoveItem(label: String, action: Runnable)

    /**
     * Добавляет пункт меню "Прочитать"
     */
    fun addReadItem(label: String, action: Runnable)

    /**
     * Добавляет пункт меню с иконкой и опциональной подписью
     */
    fun addIconItem(
        label: String? = null, icon: SbisMobileIcon.Icon,
        style: SwipeItemStyle, action: Runnable
    )

    /**
     * Добавляет пункт меню с прикладными настройками
     */
    fun addItem(item: SwipeMenuItem)
}

internal class SwipeMenuItemList : ArrayList<SwipeMenuItem>, SwipeMenuItemBuilder {

    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(c: MutableCollection<out SwipeMenuItem>) : super(c)

    override fun addRemoveItem(label: String, action: Runnable) {
        addIconItem(
            label = label,
            icon = SbisMobileIcon.Icon.smi_SwipeDelete,
            style = SwipeItemStyle.RED,
            action = action
        )
    }

    override fun addReadItem(label: String, action: Runnable) {
        addIconItem(
            label = label,
            icon = SbisMobileIcon.Icon.smi_SwipeRead,
            style = SwipeItemStyle.BLUE,
            action = action
        )
    }

    override fun addIconItem(label: String?, icon: SbisMobileIcon.Icon, style: SwipeItemStyle, action: Runnable) {
        if (label != null) {
            addItem(IconWithLabelItem(
                icon = SwipeIcon(icon),
                label = label,
                style = style,
                clickAction = {
                    action.run()
                }
            ))
        } else {
            addItem(IconItem(
                icon = SwipeIcon(icon),
                style = style,
                clickAction = {
                    action.run()
                }
            ))
        }
    }

    override fun addItem(item: SwipeMenuItem) {
        add(item)
    }

}