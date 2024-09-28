package ru.tensor.sbis.design.utils

import java.util.LinkedList

/** @SelfDocumented */
const val DEFAULT_POOL_CAPACITY = 100

/**
 * Пул view, позволяющий по возможности повторно использовать view, ранее использовавшиеся c теми же самыми данными
 *
 * @param VIEW тип хранимых view
 * @param ID идентификатор связанных данных
 *
 * @author us.bessonov
 */
class RecentlyUsedViewPool<VIEW, ID>(
    private val capacity: Int = DEFAULT_POOL_CAPACITY,
    private val viewFactory: () -> VIEW
) {

    /**
     * Контейнер доступных view, ранее использовавшихся для конкретных id
     */
    private val cache = LinkedHashMap<ID?, LinkedList<VIEW>>(capacity)

    /**
     * Контейнер view, используемых для конкретных id
     */
    private val used = LinkedHashMap<ID?, LinkedList<VIEW>>()

    /**
     * Возвращает доступный view, либо создаёт новый
     */
    fun get(id: ID?): VIEW {
        return (cache.getView(id)
            ?: cache.getAvailableView()
            ?: viewFactory.invoke())
            .also { used.addView(id, it) }
    }

    /**
     * Помещает view в пул
     */
    fun recycle(view: VIEW) {
        used.entries
            .find { it.value.remove(view) }
            ?.let {
                if (cache.getTotalSize() < capacity) {
                    cache.addView(it.key, view)
                }
            }
    }

    /**
     * Обеспечивает наличие заданного числа view в пуле, в пределах [capacity]
     */
    fun inflate(count: Int) {
        val currentSize = cache.getTotalSize()
        if (count <= currentSize) return
        repeat(count.coerceAtMost(capacity) - currentSize) {
            cache.addView(null, viewFactory.invoke())
        }
    }

    /**
     * Увеличивает число view в пуле на заданное значение, в пределах [capacity]
     */
    fun inflateBy(count: Int) {
        if (count <= 0) return
        inflate(cache.getTotalSize() + count)
    }

    /**
     * Очищает пул
     */
    fun flush() {
        cache.clear()
        used.clear()
    }

    private fun LinkedHashMap<ID?, LinkedList<VIEW>>.getTotalSize() = values.sumBy { it.size }

    private fun LinkedHashMap<ID?, LinkedList<VIEW>>.addView(id: ID?, view: VIEW) = getOrPut(id, ::LinkedList).add(view)

    private fun LinkedHashMap<ID?, LinkedList<VIEW>>.getView(id: ID?) = get(id)?.poll()

    private fun LinkedHashMap<ID?, LinkedList<VIEW>>.getAvailableView(): VIEW? {
        entries.removeIf { it.value.isEmpty() }
        val key = if (containsKey(null)) null else keys.firstOrNull() ?: return null
        return get(key)?.poll()
    }
}