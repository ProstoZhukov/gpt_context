package ru.tensor.sbis.crud4.view

import ru.tensor.sbis.crud4.ItemWithSection
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Фабрика производящая первый элемент коллекции который не является элементом основного списка.
 *
 * @author ma.kolpakov
 */
interface FirstItemFactory<T> where T : AnyItem, T : Refreshable {

    /** @SelfDocumented */
    fun create(): ItemWithSection<T>
}

/**
 * Интерфейс для обработки события SwipeRefresh.
 */
interface Refreshable {
    fun refresh() = Unit
}

/**
 * Маркерный интерфейс, позволяющий скрыть добавленый первый элемент если список пуст.
 */
interface FirstItemHiddenWhenEmpty