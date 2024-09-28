package ru.tensor.sbis.design_dialogs.dialogs.container

import androidx.annotation.ColorRes
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Интерфейс, декларирующий возможность изменять меню тулбара
 *
 * @author sa.nikitin
 */
interface HasToolbarMenu {

    /**
     * Класс параметров элемента меню тулбара
     *
     * @property id                 Идентификатор элемента
     * @property icon               Иконка элемента
     * @property iconColorResId     Цвет иконки элемента
     * @property isActive           Признак того, что элемент меню активен
     */
    data class ToolbarMenuItem @JvmOverloads constructor(
        val id: Int,
        val icon: SbisMobileIcon.Icon,
        @ColorRes val iconColorResId: Int = -1,
        val isActive: Boolean = true
    )

    /**
     * Установить элементы меню тулбара
     * Перед установкой меню очищается
     *
     * @param menuItems Список элементов меню тулбара
     */
    fun setupToolbarMenu(menuItems: List<ToolbarMenuItem>)

    /**
     * Обновить элемент
     * Если в списке нет элемента с таким же id,
     * он будет добавлен в начало списка
     * Если в списке уже есть эелемент с таким же id,
     * то эелемент устанавливается вместо него
     *
     * @param menuItem Элемент меню тулбара
     */
    fun updateMenuItem(menuItem: ToolbarMenuItem)

    /**
     * Удалить элемент по [id], если он есть в списке
     */
    fun removeMenuItemById(id: Int)

    /**
     * Очистить меню тулбара
     */
    fun clearToolbarMenu()
}