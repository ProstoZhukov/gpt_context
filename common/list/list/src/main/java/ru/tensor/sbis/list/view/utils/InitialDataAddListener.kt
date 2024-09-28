package ru.tensor.sbis.list.view.utils

import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Слушатель добавления данных в первый раз.
 */
interface InitialDataAddListener {

    fun onAdd(items: List<AnyItem>)
}