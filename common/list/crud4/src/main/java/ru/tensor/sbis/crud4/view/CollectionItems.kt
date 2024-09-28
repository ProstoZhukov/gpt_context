package ru.tensor.sbis.crud4.view

import androidx.annotation.AnyThread
import java.util.Collections

/**
 * Хранит коллекцию элементов, обеспечивая инкапсуляция и синхронизацию ее модифицирования. Пустой по-умолчанию.
 */
class CollectionItems<SOURCE_ITEM> {

    private val items = mutableListOf<SOURCE_ITEM>()

    /**
     * Размер хранящегося списка.
     */
    val size get() = items.size

    /**
     * Получить хранящийся список.
     */
    @Synchronized
    @AnyThread
    fun getAllItems(): List<SOURCE_ITEM> = items.toList()

    /**
     * Заменить содержимое хранящегося список на элементы из [newItems].
     */
    @Synchronized
    @AnyThread
    fun reset(newItems: List<SOURCE_ITEM>) {
        clear()
        items.addAll(newItems)
    }

    /**
     * В хранящемся списке удалить элементы с индексами [indexes].
     * Важно! Удаление производится последовательно по одному, то есть каждый следующий индекс должен учитывать смещение
     * от предыдущих удалений.
     */
    @Synchronized
    @AnyThread
    fun remove(indexes: List<Long>) {
        indexes.forEach {
            items.removeAt(it.toInt())
        }
    }

    /**
     * В хранящемся списке переместить элементы с индексами в ключах [indexesFromTo] на индексы в значениях пар.
     * Важно! Перемещение производится последовательно по одному, то есть каждый следующий перенос должен учитывать
     * изменение индексов от предыдущих удалений.
     */
    @Synchronized
    @AnyThread
    fun move(indexesFromTo: List<Pair<Long, Long>>) {
        indexesFromTo.forEach {
            Collections.swap(items, it.first.toInt(), it.second.toInt())
        }
    }

    /**
     * В хранящемся списке добавить элементы с индексами в ключах [indexAndItemList] и объектами в значениях пар.
     * Важно! Вставка производится последовательно по одному, то есть каждая следующая вставка должен учитывать
     * изменение индексов от предыдущих вставок.
     */
    @Synchronized
    @AnyThread
    fun add(indexAndItemList: List<Pair<Long, SOURCE_ITEM>>) {
        indexAndItemList.forEach {
            items.add(
                it.first.toInt(),
                it.second
            )
        }
    }

    /**
     * В хранящемся списке заменить элементы с индексами в ключах [indexAndItemList] на объекты в значениях пар.
     */
    @Synchronized
    @AnyThread
    fun replace(indexAndItemList: List<Pair<Long, SOURCE_ITEM>>) {
        indexAndItemList.forEach {
            items[it.first.toInt()] = it.second
        }
    }

    /**
     * Очистить хранящийся список.
     */
    @Synchronized
    @AnyThread
    fun clear() {
        items.clear()
    }
}