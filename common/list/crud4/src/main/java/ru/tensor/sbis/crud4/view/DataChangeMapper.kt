package ru.tensor.sbis.crud4.view

import androidx.annotation.AnyThread
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.datachange.ItemChanged
import ru.tensor.sbis.crud4.view.datachange.ItemInserted
import ru.tensor.sbis.crud4.view.datachange.ItemMoved
import ru.tensor.sbis.crud4.view.datachange.ItemRemoved
import ru.tensor.sbis.crud4.view.datachange.SetItems

/**
 * Маппер события обновления данных адаптера списка.
 * Выполняет маппинг только измененных элементов в из события.
 */
class DataChangeMapper<SOURCE_ITEM, OUTPUT_ITEM>(
    private val mappedCollectionItems: CollectionItems<OUTPUT_ITEM> = CollectionItems()
) {
    @AnyThread
    fun map(
        action: DataChange<SOURCE_ITEM>,
        mapItem: (SOURCE_ITEM) -> OUTPUT_ITEM,
        mapList: (List<SOURCE_ITEM>) -> List<OUTPUT_ITEM> = { list -> list.map { mapItem(it) } },
        mapIndexPairs: (List<Pair<Long, SOURCE_ITEM>>) -> List<Pair<Long, OUTPUT_ITEM>> = { list ->
            list.map {
                Pair(
                    it.first,
                    mapItem(it.second)
                )
            }
        },
    ): DataChange<OUTPUT_ITEM> {
        return when (action) {
            is SetItems -> {
                mappedCollectionItems.reset(mapList(action.allItems))
                SetItems(mappedCollectionItems.getAllItems(), action.forceScrollToInitialPosition)
            }

            is ItemChanged -> {
                val mapped = mapIndexPairs(action.indexItemList)
                mappedCollectionItems.replace(mapped)
                ItemChanged(
                    mapped,
                    mappedCollectionItems.getAllItems()
                )
            }

            is ItemInserted -> {
                val mapped = mapIndexPairs(action.indexItemList)
                mappedCollectionItems.add(mapped)
                ItemInserted(
                    mapped,
                    mappedCollectionItems.getAllItems()
                )
            }

            is ItemMoved -> {
                mappedCollectionItems.move(action.indexPairs)
                ItemMoved(
                    action.indexPairs,
                    mappedCollectionItems.getAllItems()
                )
            }

            is ItemRemoved -> {
                mappedCollectionItems.remove(action.indexes)
                ItemRemoved(
                    action.indexes,
                    mappedCollectionItems.getAllItems()
                )
            }
        }
    }
}