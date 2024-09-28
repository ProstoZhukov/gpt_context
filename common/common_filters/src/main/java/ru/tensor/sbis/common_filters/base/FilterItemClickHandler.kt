package ru.tensor.sbis.common_filters.base

/**
 * Обработчик кликов по [FilterItem]. Может быть установлен в качестве binding variable в xml и передан при вызове
 * метода onClick, если установка обработчика кликов во вьюмодели неприемлема
 */
interface FilterItemClickHandler<ITEM: FilterItem> {

    fun onItemClick(item: ITEM)
}