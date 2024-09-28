package ru.tensor.sbis.base_components.adapter.sectioned.content

/**
 * @author am.boldinov
 */
interface ListController {

    /**
     * Известен ли первый элемент секции.
     */
    fun knownHead(): Boolean

    /**
     * Известен ли последний элемент секции.
     */
    fun knownTail(): Boolean

    /**
     * Обработать изменение области видимых элементов.
     */
    fun onVisibleRangeChanged(firstVisible: Int, lastVisible: Int, direction: Int)
}