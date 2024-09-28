package ru.tensor.sbis.design.selection.ui.factories

import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.getFilterPageStartIndexForMeta

/**
 * Фабрика фильтров
 *
 * @see getFilterPageStartIndexForMeta
 *
 * @author ma.kolpakov
 */
interface FilterFactory<in DATA : SelectorItemModel, out FILTER, in ANCHOR> {

    /**
     * Метод для создания фильтра на основе текущих запросов пользователя
     */
    fun createFilter(meta: FilterMeta<DATA, ANCHOR>): FILTER
}