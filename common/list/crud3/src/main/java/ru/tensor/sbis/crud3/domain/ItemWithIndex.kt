package ru.tensor.sbis.crud3.domain

import androidx.annotation.AnyThread

/**
 * Извлекает из пары индекс и элемента коллекции микросервиса.
 * Используется для работы с классами используемых в методах наблюдателя коллекции:
 * onAdd(p0: ArrayList<[ITEM_WITH_INDEX]>) и
 * onReplace(p0: ArrayList<[ITEM_WITH_INDEX]>).
 */
@AnyThread
interface ItemWithIndex<ITEM_WITH_INDEX, ITEM> {
    /**
     * Получить индекс.
     */
    fun getIndex(itemWithIndex: ITEM_WITH_INDEX): Long

    /**
     * Получить элемент коллекции.
     */
    fun getItem(itemWithIndex: ITEM_WITH_INDEX): ITEM
}