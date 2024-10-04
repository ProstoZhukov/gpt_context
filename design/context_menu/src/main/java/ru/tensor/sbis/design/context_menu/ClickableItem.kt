package ru.tensor.sbis.design.context_menu

/**
 * Интерфейс, делающий элемент меню кликабельным.
 *
 * @author ma.kolpakov
 */
internal interface ClickableItem {

    /** Handler по нажатию на элемент. */
    var handler: (() -> Unit)?
}