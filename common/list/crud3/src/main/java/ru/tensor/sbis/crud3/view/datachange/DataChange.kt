package ru.tensor.sbis.crud3.view.datachange

/**
 * События обновления данных адаптера списка.
 */
sealed class DataChange<ITEM>(allItems: List<ITEM>) {
    val allItems: List<ITEM> = allItems.toList()
}

/**
 * Сбросить текущее состояние списка до переданного.
 */
class SetItems<ITEM>(allItems: List<ITEM>) : DataChange<ITEM>(allItems)

/**
 * Удалить элементы по индексу.
 */
class ItemRemoved<ITEM>(
    indexes: List<Long>,
    allItems: List<ITEM>
) : DataChange<ITEM>(allItems) {
    val indexes: List<Long> = indexes.toList()

    init {
        require(indexes.isNotEmpty()) { "indexes must not be empty" }
        require(allItems.isNotEmpty()) { "allItems must not be empty" }
    }
}

/**
 * Переместить элементы с позиций по индексам на другие позиции по индексам.
 */
class ItemMoved<ITEM>(
    indexPairs: List<Pair<Long, Long>>,
    allItems: List<ITEM>
) : DataChange<ITEM>(allItems) {
    val indexPairs: List<Pair<Long, Long>> = indexPairs.toList()

    init {
        require(indexPairs.isNotEmpty()) { "indexPairs must not be empty" }
        require(allItems.isNotEmpty()) { "allItems must not be empty" }
    }
}

/**
 * Заменить элементы по индексам.
 */
class ItemChanged<ITEM>(
    indexItemList: List<Pair<Long, ITEM>>,
    allItems: List<ITEM>
) : DataChange<ITEM>(allItems) {
    val indexItemList: List<Pair<Long, ITEM>> = indexItemList.toList()

    init {
        require(indexItemList.isNotEmpty()) { "indexItemList must not be empty" }
        require(allItems.isNotEmpty()) { "allItems must not be empty" }
    }
}