package ru.tensor.sbis.swipeablelayout.view

import ru.tensor.sbis.swipeablelayout.SwipeMenu
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool

/**
 * Интерфейс, описывающий функционал контейнера для отображения свайп-меню.
 *
 * @author vv.chekurda
 */
interface SwipeMenuItemsContainer {

    /**
     * Установить свайп-меню. Задание непустого меню приводит к тому, что вместо сдвига содержимого,
     * свайп справа налево открывает меню.
     * В качестве последнего пункта меню может быть задан пункт удаления. Это делает доступным
     * смахивание, в частности при интенсивном свайпе, или при отпускании меню, правая граница
     * которого немного удалена от края.
     *
     * @see [SwipeMenu]
     *
     * @param items список пунктов меню
     */
    fun <ITEM : SwipeMenuItem> setMenu(items: List<ITEM>)

    /**
     * Установить пул view пунктов меню и разделителей.
     * Возможно использовать только для меню, состоящих из элементов одного типа.
     *
     * @param menuViewPool [SwipeMenuViewPool], в котором до востребования хранятся view для меню
     */
    fun setMenuItemViewPool(menuViewPool: SwipeMenuViewPool?)

}