package ru.tensor.sbis.design.design_menu.api

/**
 * Интерфейс, делающий элемент меню кликабельным.
 *
 * @author ra.geraskin
 */
internal interface ClickableItem {

    /**
     * Обработчик кликов на элемент.
     */
    var handler: (() -> Unit)?

}