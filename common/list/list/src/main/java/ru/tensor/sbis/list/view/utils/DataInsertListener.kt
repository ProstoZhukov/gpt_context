package ru.tensor.sbis.list.view.utils

import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Слушатель вставки данных.
 *
 * @author ma.kolpakov
 */
interface DataInsertListener {
    fun onInsert(list: List<AnyItem>)
}