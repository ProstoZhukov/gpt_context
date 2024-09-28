package ru.tensor.sbis.common_filters.base

/**
 * Элемент списка фильтров
 *
 * @property type тип элемента
 * @property uuid идентификатор элемента
 * @property title текст заголовка
 */
interface FilterItem {
    val type: FilterType
    val uuid: String
    val title: String
        get() = ""

    companion object {
        val areItemsTheSame = { old: FilterItem, new: FilterItem ->
            old.type == new.type && old.uuid == new.uuid
        }
    }
}