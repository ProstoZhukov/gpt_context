package ru.tensor.sbis.design.design_menu.api

/**
 * Интерфейс для отправки кликов по меню из шторки (MovablePanel).
 * Для перехвата кликов по элементам меню, находящимся в шторке, необходимо наследоваться вызывающим фрагментом или
 * активити от данного интерфейса. Метод [onClick] , будет вызываться при клике на элемент с передачей в аргумент.
 *
 * @author ra.geraskin
 */
interface MenuItemClickListener {

    /** @SelfDocumented */
    fun onClick(item: BaseMenuItem)

}