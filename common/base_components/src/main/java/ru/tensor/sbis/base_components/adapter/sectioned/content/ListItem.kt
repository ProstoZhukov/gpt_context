package ru.tensor.sbis.base_components.adapter.sectioned.content

/**
 * Интерфейс элемента списка.
 *
 * @author am.boldinov
 */
interface ListItem {

    /**
     * Получить идентификатор элемента списка.
     */
    val itemTypeId: String

    /**
     * Сравнить содержимое элемента с содержимым другого элемента.
     *
     * @return  true - если контент одинаковый,
     *          false - если контент отличается,
     *          null - если нет специфичной логики сравнения
     */
    fun areContentsTheSame(other: Any?): Boolean?

}