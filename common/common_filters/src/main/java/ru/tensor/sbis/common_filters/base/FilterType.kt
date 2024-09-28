package ru.tensor.sbis.common_filters.base

/**
 * Тип элемента списка фильтров
 *
 * @property id числовой идентификатор типа
 */
interface FilterType {
    val id: Int
        get() = 0
}

/**
 * Неопределённый тип [FilterItem]
 */
object NoType : FilterType