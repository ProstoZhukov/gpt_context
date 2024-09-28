package ru.tensor.sbis.list.view.adapter

import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Публичный API адаптера SbisList.
 *
 * @author aa.sviridov
 */
interface SbisAdapterApi {

    /**
     * Получить [AnyItem] из адаптера.
     */
    fun getItem(topChildPosition: Int): AnyItem
}