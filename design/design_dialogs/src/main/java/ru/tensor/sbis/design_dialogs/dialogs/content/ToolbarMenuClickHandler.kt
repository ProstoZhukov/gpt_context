package ru.tensor.sbis.design_dialogs.dialogs.content

/**
 * Интерфейс обработчика кликов на элементы меню тулбара
 *
 * @author sa.nikitin
 */
interface ToolbarMenuClickHandler {

    /**
     * Элемент меню тулбара с идентификатором [menuItemId] был кликнут
     */
    fun onToolbarMenuItemClick(menuItemId: Int)
}