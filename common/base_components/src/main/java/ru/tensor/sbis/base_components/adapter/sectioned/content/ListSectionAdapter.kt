package ru.tensor.sbis.base_components.adapter.sectioned.content

/**
 * @author am.boldinov
 */
interface ListSectionAdapter<T : ListItem> {

    /**
     * Получить элемент списка на указанной позиции.
     */
    fun getSectionItem(position: Int): T?

}